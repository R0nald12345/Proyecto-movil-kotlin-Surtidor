package com.example.parcialproyectosurtidor.presentacion.Surtidor



import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView // Importar la clase TextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.semantics.text
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
    private lateinit var etCantidadBombas: EditText // Todavía puedes usar este para agregar nuevos
    private lateinit var etCantidadLitros: EditText // Necesitas un EditText para Litros también
    private lateinit var spinnerTipoCombustible: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnAgregarCombustible: Button // Botón para agregar nuevo tipo de combustible
    private lateinit var layoutCombustiblesExistentes: LinearLayout // Layout para mostrar y editar stocks existentes
    private lateinit var layoutAgregarNuevoCombustible: LinearLayout // Layout que contiene el spinner y los EditText para agregar

    private lateinit var nSurtidor: NSurtidor
    private lateinit var nTipoCombustible: NTipoCombustible
    private lateinit var nStockCombustible: NStockCombustible

    private var surtidorId: Int? = null
    private var surtidor: Surtidor? = null
    private var puntoSeleccionado: Point? = null

    // Lista para mantener los stocks de combustible actuales del surtidor
    private var stocksActuales: MutableList<StockCombustible> = mutableListOf()

    // Lista para los nuevos combustibles agregados (antes de guardar)
    private val nuevosCombustiblesAgregados = mutableListOf<Triple<Int, Int, Double>>() // ID Tipo, Bombas, Litros

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_surtidor)

        // Inicializar vistas (estas sí deben inicializarse primero para poder usar mapView)
        mapView = findViewById(R.id.mapView)
        etNombreSurtidor = findViewById(R.id.etNombreSurtidor)
        etCantidadBombas = findViewById(R.id.etCantidadBombas)
        etCantidadLitros = findViewById(R.id.etCantidadLitros)
        spinnerTipoCombustible = findViewById(R.id.spinnerTipoCombustible)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnAgregarCombustible = findViewById(R.id.btnAgregarCombustible)
        layoutCombustiblesExistentes = findViewById(R.id.layoutCombustiblesExistentes)
        layoutAgregarNuevoCombustible = findViewById(R.id.layoutAgregarNuevoCombustible)

        // Inicializar las capas de negocio
        nSurtidor = NSurtidor(this)
        nTipoCombustible = NTipoCombustible(this)
        nStockCombustible = NStockCombustible(this)

        // Inicializar el manager de anotaciones del mapa AQUI
        annotationManager = mapView.annotations.createPointAnnotationManager()

        // Configurar el listener de clic en el mapa AQUI también, ya que necesita annotationManager
        mapView.gestures.addOnMapClickListener { point ->
            annotationManager.deleteAll()
            val icono = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
            val resized = Bitmap.createScaledBitmap(icono, 80, 80, false)
            val marker = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(resized)
                .withTextField("Nueva Ubicación")
            annotationManager.create(marker)
            puntoSeleccionado = point
            Toast.makeText(this, "Nueva ubicación seleccionada", Toast.LENGTH_SHORT).show()
            true
        }

        // Obtener el ID del surtidor a editar
        surtidorId = intent.getIntExtra("SURTIDOR_ID", -1)

        if (surtidorId != -1) {
            surtidor = nSurtidor.obtenerPorId(surtidorId!!)
            surtidor?.let {
                // Cargar los datos del surtidor en los campos de entrada
                etNombreSurtidor.setText(it.nombre)
                puntoSeleccionado = Point.fromLngLat(it.longitud, it.latitud)

                // Configurar la cámara del mapa AQUI, después de obtener puntoSeleccionado
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(puntoSeleccionado ?: Point.fromLngLat(-63.18, -17.78))
                        .zoom(puntoSeleccionado?.let { 15.0 } ?: 12.0)
                        .build()
                )

                mostrarMarcadorEnMapa(puntoSeleccionado!!) // Ahora annotationManager ya está inicializado

                // Cargar y mostrar los stocks de combustible existentes
                cargarStocksExistentes(it.id)

            } ?: run {
                Toast.makeText(this, "Surtidor no encontrado", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            // Si el ID no es válido, configura el mapa con una ubicación predeterminada
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(-63.18, -17.78))
                    .zoom(12.0)
                    .build()
            )
            Toast.makeText(this, "ID de surtidor no válido", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Cargar los tipos de combustible en el spinner (para agregar nuevos)
        cargarTiposDeCombustible()

        // Configurar el listener para agregar nuevo combustible
        btnAgregarCombustible.setOnClickListener {
            agregarNuevoTipoCombustible()
        }

        // Guardar los cambios
        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }


    // Función para mostrar el marcador existente
    private fun mostrarMarcadorEnMapa(point: Point) {
        annotationManager.deleteAll()
        val icono = BitmapFactory.decodeResource(resources, R.drawable.red_marker) // Reemplaza con tu drawable
        val resized = Bitmap.createScaledBitmap(icono, 80, 80, false) // Ajusta el tamaño
        // Crear el marcador con icono
        val marker = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(resized)
            .withTextField(surtidor?.nombre ?: "Surtidor") // Usar el nombre del surtidor si está disponible

        // Crear la anotación en el mapa
        annotationManager.create(marker)
    }

    private fun cargarTiposDeCombustible() {
        val tipos = nTipoCombustible.obtenerTodos() // Obtener todos los tipos de combustible
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos.map { it.nombre })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoCombustible.adapter = spinnerAdapter
    }

    private fun cargarStocksExistentes(surtidorId: Int) {
        stocksActuales = nStockCombustible.obtenerPorSurtidor(surtidorId).toMutableList()
        val tiposCombustible = nTipoCombustible.obtenerTodos()

        layoutCombustiblesExistentes.removeAllViews() // Limpiar vistas anteriores

        for (stock in stocksActuales) {
            val tipoCombustible = tiposCombustible.find { it.id == stock.idTipoCombustible }

            val itemView = layoutInflater.inflate(R.layout.item_editar_tipo_combustible, null) // Necesitas crear este layout
            val tvTipoCombustible = itemView.findViewById<TextView>(R.id.tvTipoCombustible)
            val etCantidadBombasExistente = itemView.findViewById<EditText>(R.id.etCantidadBombasExistente) // EditText para editar bombas
            val etCantidadLitrosExistente = itemView.findViewById<EditText>(R.id.etCantidadLitrosExistente) // EditText para editar litros
            val btnEliminarCombustible = itemView.findViewById<Button>(R.id.btnEliminarCombustible) // Botón para eliminar

            tvTipoCombustible.text = tipoCombustible?.nombre ?: "Desconocido"
            etCantidadBombasExistente.setText(stock.nroBombas.toString())
            etCantidadLitrosExistente.setText(stock.cantidad.toString())

            btnEliminarCombustible.setOnClickListener {
                // Eliminar la vista y marcar el stock para eliminación
                layoutCombustiblesExistentes.removeView(itemView)
                stocksActuales.remove(stock) // Remover de la lista para marcarlo como eliminado
                // Considera agregar una lista de IDs de stocks a eliminar si necesitas distinguirlos al guardar
            }

            // Asignar el objeto StockCombustible a la vista para fácil acceso al guardar
            itemView.tag = stock

            layoutCombustiblesExistentes.addView(itemView)
        }
    }

    private fun agregarNuevoTipoCombustible() {
        val tipoCombustibleIndex = spinnerTipoCombustible.selectedItemPosition
        val cantidadBombasStr = etCantidadBombas.text.toString().trim()
        val cantidadLitrosStr = etCantidadLitros.text.toString().trim()

        val cantidadBombas = cantidadBombasStr.toIntOrNull()
        val cantidadLitros = cantidadLitrosStr.toDoubleOrNull()

        if (cantidadBombas == null || cantidadBombas <= 0) {
            Toast.makeText(this, "La cantidad de bombas debe ser un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (cantidadLitros == null || cantidadLitros <= 0) {
            Toast.makeText(this, "La cantidad de litros debe ser un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (tipoCombustibleIndex < 0) {
            Toast.makeText(this, "Debe seleccionar un tipo de combustible", Toast.LENGTH_SHORT).show()
            return
        }

        val tiposCombustible = nTipoCombustible.obtenerTodos()
        val tipoCombustibleSeleccionado = tiposCombustible[tipoCombustibleIndex]
        val idTipoCombustible = tipoCombustibleSeleccionado.id

        // Verificar si este tipo de combustible ya existe en los stocks actuales o en los nuevos agregados
        val yaExiste = stocksActuales.any { it.idTipoCombustible == idTipoCombustible } ||
                nuevosCombustiblesAgregados.any { it.first == idTipoCombustible }

        if (yaExiste) {
            Toast.makeText(this, "Este tipo de combustible ya ha sido agregado", Toast.LENGTH_SHORT).show()
            return
        }


        // Agregar a la lista de nuevos combustibles
        nuevosCombustiblesAgregados.add(Triple(idTipoCombustible, cantidadBombas, cantidadLitros))

        // Agregar una vista para el nuevo combustible (similar a los existentes, pero quizás con un indicador visual de "nuevo")
        val itemView = layoutInflater.inflate(R.layout.item_editar_tipo_combustible, null) // Reutilizamos el layout
        val tvTipoCombustible = itemView.findViewById<TextView>(R.id.tvTipoCombustible)
        val etCantidadBombasExistente = itemView.findViewById<EditText>(R.id.etCantidadBombasExistente)
        val etCantidadLitrosExistente = itemView.findViewById<EditText>(R.id.etCantidadLitrosExistente)
        val btnEliminarCombustible = itemView.findViewById<Button>(R.id.btnEliminarCombustible)

        tvTipoCombustible.text = tipoCombustibleSeleccionado.nombre
        etCantidadBombasExistente.setText(cantidadBombas.toString())
        etCantidadLitrosExistente.setText(cantidadLitros.toString())

        // Marcar esta vista como "nueva" para el proceso de guardado
        itemView.tag = Triple(idTipoCombustible, cantidadBombas, cantidadLitros) // O una clase de datos más específica

        btnEliminarCombustible.setOnClickListener {
            // Eliminar la vista y remover de la lista de nuevos agregados
            layoutCombustiblesExistentes.removeView(itemView)
            nuevosCombustiblesAgregados.remove(itemView.tag as Triple<Int, Int, Double>)
        }

        layoutCombustiblesExistentes.addView(itemView)

        etCantidadBombas.text.clear()
        etCantidadLitros.text.clear()
        spinnerTipoCombustible.setSelection(0) // Resetear spinner
    }



    private fun guardarCambios() {
        val nombre = etNombreSurtidor.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre del surtidor no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (puntoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar una ubicación en el mapa", Toast.LENGTH_SHORT).show()
            return
        }

        val surtidorEditado = surtidor?.copy(
            nombre = nombre,
            latitud = puntoSeleccionado!!.latitude(),
            longitud = puntoSeleccionado!!.longitude()
        )

        surtidorEditado?.let {
            // 1. Actualizar el surtidor
            val surtidorActualizado = nSurtidor.editar(it)

            if (surtidorActualizado) {
                // 2. Procesar los stocks de combustible

                // Stocks que fueron eliminados (los que ya no están en stocksActuales)
                val stocksParaEliminar = nStockCombustible.obtenerPorSurtidor(it.id)
                    .filter { original -> stocksActuales.none { it.id == original.id } }

                for (stockAEliminar in stocksParaEliminar) {
                    nStockCombustible.eliminar(stockAEliminar.id)
                }

                // Stocks existentes que pudieron ser modificados
                for (i in 0 until layoutCombustiblesExistentes.childCount) {
                    val itemView = layoutCombustiblesExistentes.getChildAt(i)
                    val tag = itemView.tag // Obtener el tag que guardamos (objeto StockCombustible original o Triple para nuevos)

                    if (tag is StockCombustible) { // Es un stock existente
                        val stockOriginal = tag
                        val etCantidadBombasExistente = itemView.findViewById<EditText>(R.id.etCantidadBombasExistente)
                        val etCantidadLitrosExistente = itemView.findViewById<EditText>(R.id.etCantidadLitrosExistente)

                        val nuevaCantidadBombas = etCantidadBombasExistente.text.toString().trim().toIntOrNull()
                        val nuevaCantidadLitros = etCantidadLitrosExistente.text.toString().trim().toDoubleOrNull()

                        if (nuevaCantidadBombas == null || nuevaCantidadBombas <= 0) {
                            Toast.makeText(this, "Cantidad de bombas no válida para un tipo de combustible.", Toast.LENGTH_SHORT).show()
                            // Considera alguna lógica para manejar este error, quizás no guardar y notificar al usuario
                            return // Detenemos el proceso de guardado si hay un error de validación
                        }

                        if (nuevaCantidadLitros == null || nuevaCantidadLitros <= 0) {
                            Toast.makeText(this, "Cantidad de litros no válida para un tipo de combustible.", Toast.LENGTH_SHORT).show()
                            // Considera alguna lógica para manejar este error
                            return // Detenemos el proceso de guardado
                        }

                        // Verificar si ha habido cambios en este stock existente
                        if (stockOriginal.nroBombas != nuevaCantidadBombas || stockOriginal.cantidad != nuevaCantidadLitros) {
                            val stockModificado = stockOriginal.copy(
                                nroBombas = nuevaCantidadBombas,
                                cantidad = nuevaCantidadLitros
                            )
                          nStockCombustible.editar(stockModificado) // Asumiendo que tienes un método editar en NStockCombustible que toma un objeto StockCombustible
                        }
                    }
                }
                // 3. Insertar los nuevos stocks agregados
                for (nuevoCombustibleData in nuevosCombustiblesAgregados) {
                    val idTipoCombustible = nuevoCombustibleData.first
                    val cantidadBombas = nuevoCombustibleData.second
                    val cantidadLitros = nuevoCombustibleData.third

                    val nuevoStock = StockCombustible(
                        id = 0, // El ID será generado por la base de datos
                        idSurtidor = it.id,
                        idTipoCombustible = idTipoCombustible,
                        nroBombas = cantidadBombas,
                        cantidad = cantidadLitros
                    )
                    nStockCombustible.crear(nuevoStock) // Asumiendo que tienes un método crear en NStockCombustible
                }

                Toast.makeText(this, "Surtidor actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish() // Volver a la actividad anterior
            } else {
                Toast.makeText(this, "Error al actualizar el surtidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Métodos del ciclo de vida del mapa
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