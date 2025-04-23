package com.example.parcialproyectosurtidor.presentacion.Surtidor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.example.parcialproyectosurtidor.negocio.NStockCombustible
import com.example.parcialproyectosurtidor.negocio.NTipoCombustible
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class VerSurtidorActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var nSurtidor: NSurtidor
    private lateinit var nStockCombustible: NStockCombustible
    private lateinit var nTipoCombustible: NTipoCombustible
    private lateinit var layoutCombustible: LinearLayout
    private lateinit var tvNombreSurtidor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_surtidor)

        // Inicializar vistas
        mapView = findViewById(R.id.mapView)
        layoutCombustible = findViewById(R.id.layoutCombustible)
        tvNombreSurtidor = findViewById(R.id.tvNombreSurtidor)

        // Inicializar el administrador de anotaciones para el mapa
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

        // Inicializar objetos de negocio
        nSurtidor = NSurtidor(this)
        nStockCombustible = NStockCombustible(this)
        nTipoCombustible = NTipoCombustible(this)

        // Obtener ID del surtidor pasado por Intent
        val surtidorId = intent.getIntExtra("SURTIDOR_ID", -1)

        if (surtidorId != -1) {
            // Cargar el surtidor
            val surtidor = nSurtidor.obtenerPorId(surtidorId)
            surtidor?.let {
                mostrarSurtidorEnMapa(it)
                mostrarDetallesSurtidor(it)
            }
        } else {
            finish() // Cerrar si no hay ID válido
        }

        // Volver a la lista
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }


    private fun mostrarSurtidorEnMapa(surtidor: Surtidor) {
        // Centro el mapa en la ubicación del surtidor
        val punto = Point.fromLngLat(surtidor.longitud, surtidor.latitud)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(punto)
                .zoom(15.0)
                .build()
        )

        // Convertir recurso drawable a bitmap
        val originalBitmap = BitmapFactory.decodeResource(
            resources, R.drawable.red_marker // Asegúrate de tener este recurso o cambiarlo por uno que tengas
        )

        // Redimensionar el bitmap para que sea más pequeño
        val resizedBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            (originalBitmap.width * 0.07).toInt(),
            (originalBitmap.height * 0.07).toInt(),
            true
        )

        // Crear el marcador con icono
        val marker = PointAnnotationOptions()
            .withPoint(punto)
            .withIconImage(resizedBitmap)
            .withTextField(surtidor.nombre)

        // Crear la anotación en el mapa
        pointAnnotationManager.create(marker)
    }

    private fun mostrarDetallesSurtidor(surtidor: Surtidor) {
        // Mostrar nombre del surtidor
        tvNombreSurtidor.text = surtidor.nombre

        // Cargar y mostrar los detalles de los combustibles
        val stockCombustibles = nStockCombustible.obtenerPorSurtidor(surtidor.id)
        val tiposCombustible = nTipoCombustible.obtenerTodos()

        for (stock in stockCombustibles) {
            val tipoCombustible = tiposCombustible.find { it.id == stock.idTipoCombustible }

            val itemView = layoutInflater.inflate(R.layout.item_ver_surtidor_tipo_combustible, null)
            val tvTipoCombustible = itemView.findViewById<TextView>(R.id.tvTipoCombustible)
            val tvCantidadBombas = itemView.findViewById<TextView>(R.id.tvCantidadBombas)

            tvTipoCombustible.text = tipoCombustible?.nombre ?: "Desconocido"
            tvCantidadBombas.text = "Cantidad de Bombas: ${stock.nroBombas}"

            layoutCombustible.addView(itemView)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}