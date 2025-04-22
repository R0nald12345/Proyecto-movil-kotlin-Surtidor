package com.example.parcialproyectosurtidor.presentacion

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
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

class AgregarSurtidorEnMapaActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var nSurtidor: NSurtidor
    private var puntoSeleccionado: Point? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_surtidor_en_mapa)

        mapView = findViewById(R.id.mapView)
        nSurtidor = NSurtidor(this)
        annotationManager = mapView.annotations.createPointAnnotationManager()

        // Centrar el mapa
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-63.18, -17.78))
                .zoom(12.0)
                .build()
        )

        // Escuchar clics en el mapa
        mapView.gestures.addOnMapClickListener { point ->
            annotationManager.deleteAll() // eliminar marcadores anteriores

            // Icono personalizado
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
            val resized = Bitmap.createScaledBitmap(bitmap, 80, 80, false)

            // Crear el marcador
            val marker = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(resized)
                .withTextField("Nuevo Surtidor")

            annotationManager.create(marker)
            puntoSeleccionado = point

            //mostrarDialogoIngresoDatos(point)
            true
        }
    }

    // Muestra un diálogo con dos campos: nombre y cantidad de bombas
   /* private fun mostrarDialogoIngresoDatos(punto: Point) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val inputNombre = EditText(this)
        inputNombre.hint = "Nombre del surtidor"

        val inputBombas = EditText(this)
        inputBombas.hint = "Cantidad de bombas"
        inputBombas.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        layout.setPadding(32, 32, 32, 0)
        layout.addView(inputNombre)
        layout.addView(inputBombas)

        AlertDialog.Builder(this)
            .setTitle("Nuevo Surtidor")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = inputNombre.text.toString().trim()
                val bombasTexto = inputBombas.text.toString().trim()

                if (nombre.isNotEmpty() && bombasTexto.isNotEmpty()) {
                    val bombas = bombasTexto.toIntOrNull()
                    if (bombas != null && bombas > 0) {
                        val nuevo = Surtidor(
                            id = 0, // El ID se genera automáticamente
                            nombre = nombre,
                            latitud = punto.latitude(),
                            longitud = punto.longitude(),
                            cantidadBombas = bombas
                        )

                        if (nSurtidor.crear(nuevo)) {
                            Toast.makeText(this, "Surtidor guardado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    */
}
