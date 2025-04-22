package com.example.parcialproyectosurtidor.presentacion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.gestures
import android.graphics.BitmapFactory
import android.graphics.Bitmap

class EditarSurtidorActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var etNombre: EditText
    private lateinit var etCantidadBombas: EditText
    private lateinit var btnGuardar: Button
    private lateinit var nSurtidor: NSurtidor
    private var surtidor: Surtidor? = null
    private var marcador: PointAnnotation? = null
    private var coordenadaSeleccionada: Point? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editar_surtidor)

        // Vincular vistas
        mapView = findViewById(R.id.mapView)
        etNombre = findViewById(R.id.etNombre)
        etCantidadBombas = findViewById(R.id.etCantidadBombas) // nuevo campo en XML
        btnGuardar = findViewById(R.id.btnGuardar)

        nSurtidor = NSurtidor(this)
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

        /*
        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            surtidor = nSurtidor.getSurtidorPorId(id)
            surtidor?.let { cargarDatosEnMapa(it) }
        }

        mapView.gestures.addOnMapClickListener { point ->
            agregarOMoverMarcador(point)
            true
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

         */
    }

    /*
    private fun cargarDatosEnMapa(surtidor: Surtidor) {
        etNombre.setText(surtidor.nombre)
        etCantidadBombas.setText(surtidor.cantidadBombas.toString()) // mostrar bombas
        coordenadaSeleccionada = Point.fromLngLat(surtidor.longitud, surtidor.latitud)

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(coordenadaSeleccionada!!)
                .zoom(14.0)
                .build()
        )

        agregarOMoverMarcador(coordenadaSeleccionada!!)
    }
    */


    private fun agregarOMoverMarcador(point: Point) {
        val icono = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
        val iconoReducido = Bitmap.createScaledBitmap(icono, 80, 80, true)

        // Borrar marcador anterior
        marcador?.let { pointAnnotationManager.delete(it) }

        val options = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(iconoReducido)
            .withTextField("Ubicación")

        marcador = pointAnnotationManager.create(options)
        coordenadaSeleccionada = point
    }

    /*
    private fun guardarCambios() {
        val nombre = etNombre.text.toString().trim()
        val bombasTexto = etCantidadBombas.text.toString().trim()

        if (nombre.isBlank() || coordenadaSeleccionada == null || surtidor == null || bombasTexto.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidadBombas = bombasTexto.toIntOrNull()
        if (cantidadBombas == null || cantidadBombas <= 0) {
            Toast.makeText(this, "Cantidad de bombas inválida", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar los datos del surtidor
        surtidor!!.nombre = nombre
        surtidor!!.latitud = coordenadaSeleccionada!!.latitude()
        surtidor!!.longitud = coordenadaSeleccionada!!.longitude()
        surtidor!!.cantidadBombas = cantidadBombas

        if (nSurtidor.editar(surtidor!!)) {
            Toast.makeText(this, "Surtidor actualizado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        }
    }

     */
}
