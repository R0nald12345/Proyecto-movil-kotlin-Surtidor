package com.example.parcialproyectosurtidor.presentacion.Surtidor


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures

class AgregarSurtidorActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var nSurtidor: NSurtidor
    private var puntoSeleccionado: Point? = null
    private lateinit var etNombre: EditText
    private lateinit var etCantidadBombas: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_surtidor)

        mapView = findViewById(R.id.mapView)
        //etNombre = findViewById(R.id.etNombre)
        //etCantidadBombas = findViewById(R.id.etCantidadBombas)
        //btnGuardar = findViewById(R.id.btnGuardar)

        nSurtidor = NSurtidor(this)
        annotationManager = mapView.annotations.createPointAnnotationManager()

        // Centrar el mapa en una ubicación predeterminada
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-63.18, -17.78))
                .zoom(12.0)
                .build()
        )

        // Escuchar clics en el mapa
        mapView.gestures.addOnMapClickListener { point ->
            annotationManager.deleteAll() // Eliminar marcadores anteriores

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
            val resized = Bitmap.createScaledBitmap(bitmap, 80, 80, false)

            val marker = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(resized)
                .withTextField("Nuevo Surtidor")

            annotationManager.create(marker)
            puntoSeleccionado = point

            true
        }

        btnGuardar.setOnClickListener {
            guardarNuevoSurtidor()
        }
    }

    // Guardar el nuevo surtidor
    private fun guardarNuevoSurtidor() {
        val nombre = etNombre.text.toString().trim()
        val bombas = etCantidadBombas.text.toString().trim().toIntOrNull()

        if (nombre.isNotEmpty() && bombas != null && puntoSeleccionado != null) {
            val nuevoSurtidor = Surtidor(
                id = 0, // ID auto-generado
                nombre = nombre,
                latitud = puntoSeleccionado!!.latitude(),
                longitud = puntoSeleccionado!!.longitude()
            )

            if (nSurtidor.crear(nuevoSurtidor)) {
                Toast.makeText(this, "Surtidor agregado correctamente", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad
            } else {
                Toast.makeText(this, "Error al guardar el surtidor", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}