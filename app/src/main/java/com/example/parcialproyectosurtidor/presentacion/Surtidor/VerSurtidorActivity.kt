package com.example.parcialproyectosurtidor.presentacion.Surtidor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var nSurtidor: NSurtidor
    private lateinit var nStockCombustible: NStockCombustible
    private lateinit var nTipoCombustible: NTipoCombustible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_surtidor)

        // Inicializar objetos de negocio
        nSurtidor = NSurtidor(this)
        nStockCombustible = NStockCombustible(this)
        nTipoCombustible = NTipoCombustible(this)

        // Configurar mapa
        mapView = findViewById(R.id.mapView)
        annotationManager = mapView.annotations.createPointAnnotationManager()

        // Obtener ID del surtidor pasado por Intent
        val surtidorId = intent.getIntExtra("SURTIDOR_ID", -1)

        if (surtidorId != -1) {
            // Cargar el surtidor
            val surtidor = nSurtidor.obtenerPorId(surtidorId)
            surtidor?.let {
                mostrarSurtidorEnMapa(it)
            }
        } else {
            finish() // Cerrar si no hay ID válido
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

        // Crear el marcador
        val icono = ContextCompat.getDrawable(this, R.drawable.red_marker)
        val resized = Bitmap.createScaledBitmap((icono as BitmapDrawable).bitmap, 80, 80, false)

        val marker = PointAnnotationOptions()
            .withPoint(punto)
            .withIconImage(resized)
            .withTextField(surtidor.nombre)

        annotationManager.create(marker)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}