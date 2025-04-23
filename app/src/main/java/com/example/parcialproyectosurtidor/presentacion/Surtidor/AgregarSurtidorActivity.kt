package com.example.parcialproyectosurtidor.presentacion.Surtidor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
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

class AgregarSurtidorActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var etNombreSurtidor: EditText
    private lateinit var etCantidadBombas: EditText
    private lateinit var spinnerTipoCombustible: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnAgregarCombustible: Button
    private lateinit var layoutTipoCombustible: LinearLayout

    private lateinit var nSurtidor: NSurtidor
    private lateinit var nTipoCombustible: NTipoCombustible
    private lateinit var nStockCombustible: NStockCombustible
    private var puntoSeleccionado: Point? = null

    private val combustiblesSeleccionados = mutableListOf<Pair<Int, Int>>() // Lista para almacenar tipos de combustible y número de bombas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_surtidor)

        // Inicializamos vistas
        mapView = findViewById(R.id.mapView)
        etNombreSurtidor = findViewById(R.id.etNombreSurtidor)
        etCantidadBombas = findViewById(R.id.etCantidadBombas)
        spinnerTipoCombustible = findViewById(R.id.spinnerTipoCombustible)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnAgregarCombustible = findViewById(R.id.btnAgregarCombustible)
        layoutTipoCombustible = findViewById(R.id.layoutTipoCombustible)

        // Inicializamos las capas de negocio
        nSurtidor = NSurtidor(this)
        nTipoCombustible = NTipoCombustible(this)
        nStockCombustible = NStockCombustible(this)

        // Cargar los tipos de combustible en el spinner
        cargarTiposDeCombustible()

        // Manejamos el clic del botón para agregar combustible
        btnAgregarCombustible.setOnClickListener {
            agregarTipoCombustible()
        }

        // Guardamos el nuevo surtidor
        btnGuardar.setOnClickListener {
            guardarNuevoSurtidor()
        }

        // Inicializamos el mapa
        annotationManager = mapView.annotations.createPointAnnotationManager()

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-63.18, -17.78))
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
                .withTextField("Nuevo Surtidor")
            annotationManager.create(marker)
            puntoSeleccionado = point
            Toast.makeText(this, "Ubicación seleccionada", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun cargarTiposDeCombustible() {
        val tipos = nTipoCombustible.obtenerTodos() // Cargar tipos de combustible desde la BD
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos.map { it.nombre })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoCombustible.adapter = spinnerAdapter
    }

    // Agregar un tipo de combustible con su cantidad de bombas
    private fun agregarTipoCombustible() {
        val tipoCombustibleIndex = spinnerTipoCombustible.selectedItemPosition
        val cantidadBombasStr = etCantidadBombas.text.toString().trim()

        val cantidadBombas = cantidadBombasStr.toIntOrNull()

        if (cantidadBombas == null || cantidadBombas <= 0) {
            Toast.makeText(this, "La cantidad de bombas debe ser un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (tipoCombustibleIndex < 0) {
            Toast.makeText(this, "Debe seleccionar un tipo de combustible", Toast.LENGTH_SHORT).show()
            return
        }

        val tiposCombustible = nTipoCombustible.obtenerTodos()
        val idTipoCombustible = tiposCombustible[tipoCombustibleIndex].id

        combustiblesSeleccionados.add(Pair(idTipoCombustible, cantidadBombas))

        // Agregar un nuevo layout con el tipo de combustible y la cantidad de bombas
        val itemView = layoutInflater.inflate(R.layout.item_agregar_tipo_combustible, null)
        val tvTipoCombustible = itemView.findViewById<TextView>(R.id.tvTipoCombustible)
        val tvCantidadBombas = itemView.findViewById<TextView>(R.id.tvCantidadBombas)

        tvTipoCombustible.text = tiposCombustible[tipoCombustibleIndex].nombre
        tvCantidadBombas.text = "Cantidad de Bombas: $cantidadBombas"

        layoutTipoCombustible.addView(itemView)

        etCantidadBombas.text.clear() // Limpiar campo de cantidad de bombas
    }

    // Guardar el nuevo surtidor con los combustibles seleccionados
    private fun guardarNuevoSurtidor() {
        val nombre = etNombreSurtidor.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre del surtidor no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (puntoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar una ubicación en el mapa", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear surtidor
        val nuevoSurtidor = Surtidor(
            id = 0,
            nombre = nombre,
            latitud = puntoSeleccionado!!.latitude(),
            longitud = puntoSeleccionado!!.longitude()
        )

        val idSurtidorInsertado = nSurtidor.crear(nuevoSurtidor)
        if (idSurtidorInsertado) {
            val ultimoSurtidor = nSurtidor.obtenerTodos().maxByOrNull { it.id }

            if (ultimoSurtidor != null) {
                // Insertar stock para cada tipo de combustible
                for (combustible in combustiblesSeleccionados) {
                    val stockCombustible = StockCombustible(
                        id = 0,
                        idSurtidor = ultimoSurtidor.id,
                        idTipoCombustible = combustible.first,
                        cantidad = combustible.second.toDouble() * 100,
                        nroBombas = combustible.second
                    )

                    if (!nStockCombustible.crear(stockCombustible)) {
                        Toast.makeText(this, "Error al guardar el stock de combustible", Toast.LENGTH_SHORT).show()
                        return
                    }
                }

                Toast.makeText(this, "Surtidor agregado correctamente", Toast.LENGTH_SHORT).show()
                finish() // Cerrar actividad
            }
        }
    }
}
