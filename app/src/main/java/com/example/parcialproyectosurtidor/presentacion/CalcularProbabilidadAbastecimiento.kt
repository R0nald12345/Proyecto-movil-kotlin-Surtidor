package com.example.parcialproyectosurtidor.presentacion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.negocio.NStockCombustible
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.example.parcialproyectosurtidor.negocio.NTipoCombustible
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.gestures
import kotlin.math.ceil

class CalcularProbabilidadAbastecimientoActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var spinnerTipo: Spinner
    private lateinit var btnCalcular: Button
    private lateinit var txtDistancia: TextView

    private lateinit var nSurtidor: NSurtidor
    private lateinit var nTipo: NTipoCombustible
    private lateinit var nStock: NStockCombustible

    private lateinit var lineAnnotationManager: PolylineAnnotationManager
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private var drawnPoints = mutableListOf<Point>()
    private var surtidorSeleccionado: Surtidor? = null
    private var tiposCombustible = listOf<com.example.parcialproyectosurtidor.datos.entidades.TipoCombustible>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_probabilidad_abastecimiento)

        mapView = findViewById(R.id.mapView)
        spinnerTipo = findViewById(R.id.spinnerTipoCombustible)
        btnCalcular = findViewById(R.id.btnCalcular)
        txtDistancia = findViewById(R.id.txtDistancia)

        nSurtidor = NSurtidor(this)
        nTipo = NTipoCombustible(this)
        nStock = NStockCombustible(this)

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            val annotationApi = mapView.annotations
            lineAnnotationManager = annotationApi.createPolylineAnnotationManager()
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            cargarMarcadoresSurtidores()

            mapView.gestures.addOnMapClickListener { point ->
                if (surtidorSeleccionado != null) {
                    drawnPoints.clear()
                    drawnPoints.add(Point.fromLngLat(surtidorSeleccionado!!.longitud, surtidorSeleccionado!!.latitud))
                    drawnPoints.add(point)
                    drawLine()
                } else {
                    Toast.makeText(this, "Primero selecciona un surtidor", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }

        cargarTiposCombustible()
        btnCalcular.setOnClickListener { realizarCalculo() }
    }

    private fun cargarTiposCombustible() {
        tiposCombustible = nTipo.obtenerTodos()
        val nombres = tiposCombustible.map { it.nombre }
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombres)
    }

    private fun cargarMarcadoresSurtidores() {
        pointAnnotationManager.deleteAll()
        val surtidores = nSurtidor.obtenerTodos()
        val resizedBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.red_marker), 80, 80, true
        )

        val mapaSurtidores = mutableMapOf<String, Surtidor>()

        for (surtidor in surtidores) {
            val annotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(surtidor.longitud, surtidor.latitud))
                .withIconImage(resizedBitmap)
                .withTextField(surtidor.nombre)

            val annotation = pointAnnotationManager.create(annotationOptions)

            // Guardamos el texto (nombre) como clave para luego identificar el surtidor seleccionado
            mapaSurtidores[surtidor.nombre] = surtidor
        }

        // Listener global para todos los marcadores
        pointAnnotationManager.addClickListener { clickedAnnotation ->
            val nombre = clickedAnnotation.textField ?: return@addClickListener false
            val surtidor = mapaSurtidores[nombre]
            if (surtidor != null) {
                surtidorSeleccionado = surtidor
                Toast.makeText(this, "Surtidor seleccionado: ${surtidor.nombre}", Toast.LENGTH_SHORT).show()
                true
            } else {
                false
            }
        }
    }

    private fun calcularDistancia(puntos: List<Point>): Double {
        if (puntos.size < 2) return 0.0
        var distanciaTotal = 0.0
        for (i in 0 until puntos.size - 1) {
            val resultados = FloatArray(1)
            Location.distanceBetween(
                puntos[i].latitude(), puntos[i].longitude(),
                puntos[i + 1].latitude(), puntos[i + 1].longitude(),
                resultados
            )
            distanciaTotal += resultados[0]
        }
        return distanciaTotal
    }

    private fun drawLine() {
        lineAnnotationManager.deleteAll()
        if (drawnPoints.size >= 2) {
            lineAnnotationManager.create(
                PolylineAnnotationOptions()
                    .withPoints(drawnPoints)
                    .withLineColor("#FF0000")
                    .withLineWidth(5.0)
            )
            val distancia = calcularDistancia(drawnPoints)
            txtDistancia.text = "Total ${distancia.toInt()} mts"
        }
    }

    private fun realizarCalculo() {
        if (drawnPoints.size < 2 || surtidorSeleccionado == null) {
            Toast.makeText(this, "Dibuja la distancia desde un surtidor seleccionado", Toast.LENGTH_SHORT).show()
            return
        }

        val distancia = calcularDistancia(drawnPoints)
        val cantidadAutos = (distancia / 5.0).toInt()

        val tipoSeleccionado = tiposCombustible[spinnerTipo.selectedItemPosition]

        val stock = nStock.obtenerPorSurtidor(surtidorSeleccionado!!.id)
            .firstOrNull { it.idTipoCombustible == tipoSeleccionado.id }

        if (stock == null) {
            mostrarResultado("Este surtidor no tiene ${tipoSeleccionado.nombre}")
            return
        }

        val litrosDisponibles = stock.cantidad
        val bombas = stock.nroBombas
        val litrosPorAuto = 45.0
        val tiempoPorAuto = 6.0
        val autosPorTurno = bombas * 2
        val litrosNecesarios = cantidadAutos * litrosPorAuto
        val alcanza = litrosDisponibles >= litrosNecesarios
        val tandas = ceil(cantidadAutos / autosPorTurno.toDouble())
        val tiempoEspera = (tandas * tiempoPorAuto).toInt()

        val mensaje = StringBuilder()
        mensaje.append("Tiempo de Espera: ${tiempoEspera / 60}h ${tiempoEspera % 60}min\n\n")
        mensaje.append(if (alcanza)
            "✅ Las probabilidades son altas: puedes cargar tu combustible"
        else
            "❌ Las probabilidades son bajas: el combustible probablemente no alcance")

        mostrarResultado(mensaje.toString())
    }

    private fun mostrarResultado(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Resultado del cálculo")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
