package com.example.parcialproyectosurtidor.presentacion.Surtidor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible
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
import com.mapbox.maps.plugin.gestures.gestures

class EditarSurtidorActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var etNombreSurtidor: EditText
    private lateinit var etCantidadBombas: EditText
    private lateinit var spinnerTipoCombustible: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var nSurtidor: NSurtidor
    private lateinit var nTipoCombustible: NTipoCombustible
    private lateinit var nStockCombustible: NStockCombustible
    private var surtidorId: Int? = null
    private var surtidor: Surtidor? = null
    private var puntoSeleccionado: Point? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_surtidor)

        // Inicializar vistas y objetos
        mapView = findViewById(R.id.mapView)
        etNombreSurtidor = findViewById(R.id.etNombreSurtidor)
        etCantidadBombas = findViewById(R.id.etCantidadBombas)
        spinnerTipoCombustible = findViewById(R.id.spinnerTipoCombustible)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Inicializar las capas de negocio
        nSurtidor = NSurtidor(this)
        nTipoCombustible = NTipoCombustible(this)
        nStockCombustible = NStockCombustible(this)

        // Obtener el ID del surtidor a editar
        surtidorId = intent.getIntExtra("SURTIDOR_ID", -1)

        if (surtidorId != -1) {
            surtidor = nSurtidor.obtenerSurtidorPorId(surtidorId!!)


            surtidor?.let {
                // Cargar los datos del surtidor en los campos de entrada
                etNombreSurtidor.setText(it.nombre)

                // Obtener el stock de combustible para este surtidor
                val stockList = nStockCombustible.obtenerPorSurtidor(it.id)
                if (stockList.isNotEmpty()) {
                    val primerStock = stockList[0]  // O usa el que corresponda si hay múltiples tipos
                    etCantidadBombas.setText(primerStock.nroBombas.toString())

                    // También puedes establecer el tipo de combustible en el spinner
                    val tipoCombustible = nTipoCombustible.obtenerPorId(primerStock.idTipoCombustible)
                    tipoCombustible?.let { tipo ->
                        val tiposList = nTipoCombustible.obtenerTodos()
                        val posicion = tiposList.indexOfFirst { it.id == tipo.id }
                        if (posicion != -1) {
                            spinnerTipoCombustible.setSelection(posicion)
                        }
                    }
                }

                // Resto del código para configurar el mapa...
            }
        } else {
            Toast.makeText(this, "Surtidor no encontrado", Toast.LENGTH_SHORT).show()
        }

        // Cargar los tipos de combustible
        cargarTiposDeCombustible()

        // Guardar los cambios
        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        // Inicializar el mapa
        annotationManager = mapView.annotations.createPointAnnotationManager()

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-63.18, -17.78))  // Coordenadas predeterminadas
                .zoom(12.0)
                .build()
        )

        // Escuchar clics en el mapa
        mapView.gestures.addOnMapClickListener { point ->
            annotationManager.deleteAll() // Eliminar cualquier marcador previo
            val icono = ContextCompat.getDrawable(this, R.drawable.red_marker)
            val resized = Bitmap.createScaledBitmap((icono as BitmapDrawable).bitmap, 80, 80, false)
            val marker = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(resized)
                .withTextField("Ubicación Nueva")
            annotationManager.create(marker)
            puntoSeleccionado = point
            Toast.makeText(this, "Ubicación seleccionada", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun cargarTiposDeCombustible() {
        val tipos = nTipoCombustible.obtenerTodos()  // Obtener todos los tipos de combustible
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos.map { it.nombre })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoCombustible.adapter = spinnerAdapter
    }

    private fun guardarCambios() {
        val nombre = etNombreSurtidor.text.toString().trim()
        val cantidadBombasStr = etCantidadBombas.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre del surtidor no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidadBombas = cantidadBombasStr.toIntOrNull()
        if (cantidadBombas == null || cantidadBombas <= 0) {
            Toast.makeText(this, "La cantidad de bombas debe ser un número válido mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (puntoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar una ubicación en el mapa", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear surtidor con datos validados
        val surtidorEditado = Surtidor(
            id = surtidorId!!,
            nombre = nombre,
            latitud = puntoSeleccionado!!.latitude(),
            longitud = puntoSeleccionado!!.longitude()
        )

        // Actualizar surtidor
        val surtidorActualizado = nSurtidor.editar(surtidorEditado)
        if (surtidorActualizado) {
            // Actualizar stock si es necesario
            // Implementar código para actualizar el stock de combustible si se realizan cambios

            Toast.makeText(this, "Surtidor actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()  // Volver a la actividad anterior
        } else {
            Toast.makeText(this, "Error al actualizar el surtidor", Toast.LENGTH_SHORT).show()
        }
    }
}
