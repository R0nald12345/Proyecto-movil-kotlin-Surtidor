package com.example.parcialproyectosurtidor.presentacion.Surtidor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.negocio.NStockCombustible
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.example.parcialproyectosurtidor.negocio.NTipoCombustible
import com.example.parcialproyectosurtidor.negocio.patronEstrategia.*
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.gestures
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CalcularProbabilidadAbastecimientoActivity : AppCompatActivity() {

    // Vistas y componentes del mapa
    private lateinit var mapView: MapView
    private lateinit var spinnerTipo: Spinner
    private lateinit var btnCalcular: Button
    private lateinit var txtDistancia: TextView
    private lateinit var radioGroupEstrategias: RadioGroup

    // Capas de negocio
    private lateinit var nSurtidor: NSurtidor
    private lateinit var nTipo: NTipoCombustible
    private lateinit var nStock: NStockCombustible

    // Herramientas para anotaciones en el mapa
    private lateinit var lineAnnotationManager: PolylineAnnotationManager
    private lateinit var pointAnnotationManager: PointAnnotationManager

    // Variables auxiliares
    private var routePoints = mutableListOf<Point>()
    private var surtidorSeleccionado: Surtidor? = null
    private var tiposCombustible = listOf<com.example.parcialproyectosurtidor.datos.entidades.TipoCombustible>()
    private var rutaDistancia: Double = 0.0

    // Contexto del Patrón Strategy
    private lateinit var contextoEstrategia: CalculadoraAbastecimientoContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_probabilidad_abastecimiento)

        // Inicializar vistas
        mapView = findViewById(R.id.mapView)
        spinnerTipo = findViewById(R.id.spinnerTipoCombustible)
        btnCalcular = findViewById(R.id.btnCalcular)
        txtDistancia = findViewById(R.id.txtDistancia)
        radioGroupEstrategias = findViewById(R.id.radioGroupEstrategias)

        // Inicializar negocio
        nSurtidor = NSurtidor(this)
        nTipo = NTipoCombustible(this)
        nStock = NStockCombustible(this)

        // Inicializar contexto del patrón Strategy
        contextoEstrategia = CalculadoraAbastecimientoContext()
        contextoEstrategia.setStrategy(CalculoTiempoEsperaStrategy()) // Por defecto

        // Configurar mapa y cargar datos
        setupMap()
        cargarTiposCombustible()

        // Cambiar estrategia según RadioButton seleccionado
        radioGroupEstrategias.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbTiempoEspera -> contextoEstrategia.setStrategy(CalculoTiempoEsperaStrategy())
                R.id.rbDistanciaSimple -> contextoEstrategia.setStrategy(CalculoSimpleDistanciaStrategy())
                R.id.rbIndiceConveniencia -> contextoEstrategia.setStrategy(CalculoRelacionStockDistanciaBombasStrategy())
            }
        }

        // Ejecutar cálculo con estrategia seleccionada
        btnCalcular.setOnClickListener { realizarCalculoConEstrategia() }
    }

    private fun setupMap() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            val annotationApi = mapView.annotations
            lineAnnotationManager = annotationApi.createPolylineAnnotationManager()
            pointAnnotationManager = annotationApi.createPointAnnotationManager()
            cargarMarcadoresSurtidores()

            mapView.gestures.addOnMapClickListener { point ->
                surtidorSeleccionado?.let {
                    val origen = Point.fromLngLat(it.longitud, it.latitud)
                    val destino = point
                    lineAnnotationManager.deleteAll()
                    routePoints.clear()
                    txtDistancia.text = "Calculando ruta..."
                    obtenerRuta(origen, destino)
                } ?: Toast.makeText(this, "Selecciona un surtidor", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun realizarCalculoConEstrategia() {
        if (surtidorSeleccionado == null || routePoints.size < 2) {
            Toast.makeText(this, "Selecciona un surtidor y una ruta", Toast.LENGTH_LONG).show()
            return
        }

        val tipoSeleccionado = tiposCombustible[spinnerTipo.selectedItemPosition]
        val stock = nStock.obtenerPorSurtidor(surtidorSeleccionado!!.id)
            .firstOrNull { it.idTipoCombustible == tipoSeleccionado.id }

        val resultado = contextoEstrategia.calcular(
            surtidor = surtidorSeleccionado!!,
            stock = stock,
            distancia = rutaDistancia,
            tipoCombustible = tipoSeleccionado.nombre
        )

        mostrarResultado(resultado)
    }

    private fun mostrarResultado(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Resultado del Cálculo")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
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
            val annotation = pointAnnotationManager.create(
                PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(surtidor.longitud, surtidor.latitud))
                    .withIconImage(resizedBitmap)
                    .withTextField(surtidor.nombre)
            )
            mapaSurtidores[surtidor.nombre] = surtidor
        }

        pointAnnotationManager.addClickListener { clicked ->
            mapaSurtidores[clicked.textField]?.let {
                surtidorSeleccionado = it
                routePoints.clear()
                txtDistancia.text = "Selecciona tu destino"
                true
            } ?: false
        }
    }

    private fun obtenerRuta(origen: Point, destino: Point) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.mapbox.com/directions/v5/mapbox/driving/${origen.longitude()},${origen.latitude()};${destino.longitude()},${destino.latitude()}?geometries=geojson&overview=full&access_token=${getString(R.string.mapbox_access_token)}"
                val connection = URL(url).openConnection() as HttpURLConnection
                val result = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val puntos = mutableListOf<Point>()
                val json = JSONObject(result)
                val coordinates = json.getJSONArray("routes").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates")

                for (i in 0 until coordinates.length()) {
                    val c = coordinates.getJSONArray(i)
                    puntos.add(Point.fromLngLat(c.getDouble(0), c.getDouble(1)))
                }

                withContext(Dispatchers.Main) {
                    routePoints = puntos.toMutableList()
                    dibujarRuta(routePoints)
                    rutaDistancia = calcularDistanciaRuta(routePoints)
                    txtDistancia.text = "Distancia: ${"%.0f".format(rutaDistancia)} mts"
                }

            } catch (e: Exception) {
                Log.e("Ruta", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CalcularProbabilidadAbastecimientoActivity, "No se pudo obtener la ruta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dibujarRuta(puntos: List<Point>) {
        lineAnnotationManager.deleteAll()
        if (puntos.size >= 2) {
            lineAnnotationManager.create(
                PolylineAnnotationOptions()
                    .withPoints(puntos)
                    .withLineColor("#FF0000")
                    .withLineWidth(5.0)
            )
        }
    }

    private fun calcularDistanciaRuta(puntos: List<Point>): Double {
        var total = 0.0
        for (i in 0 until puntos.size - 1) {
            val r = FloatArray(1)
            Location.distanceBetween(
                puntos[i].latitude(), puntos[i].longitude(),
                puntos[i + 1].latitude(), puntos[i + 1].longitude(), r
            )
            total += r[0]
        }
        return total
    }
}
