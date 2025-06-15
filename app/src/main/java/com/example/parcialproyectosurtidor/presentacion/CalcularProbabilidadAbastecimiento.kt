package com.example.parcialproyectosurtidor.presentacion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.semantics.text
//import androidx.compose.ui.semantics.text
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
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ceil

import com.example.parcialproyectosurtidor.negocio.patronEstrategia.CalculoProbabilidadStrategy // Importar la interfaz
import com.example.parcialproyectosurtidor.negocio.patronEstrategia.CalculoTiempoEsperaStrategy // Estrategia por defecto
import com.example.parcialproyectosurtidor.negocio.patronEstrategia.CalculoSimpleDistanciaStrategy // Nueva estrategia
import com.example.parcialproyectosurtidor.negocio.patronEstrategia.CalculoRelacionStockDistanciaBombasStrategy // Otra nueva estrategia

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
    private var routePoints = mutableListOf<Point>() // Para almacenar los puntos de la ruta
    private var surtidorSeleccionado: Surtidor? = null
    private var tiposCombustible = listOf<com.example.parcialproyectosurtidor.datos.entidades.TipoCombustible>()
    private var rutaDistancia: Double = 0.0

    // Token obtenido desde recursos
    private lateinit var MAPBOX_ACCESS_TOKEN: String


    // --- Variables para el Patrón Strategy ---
    private lateinit var currentStrategy: CalculoProbabilidadStrategy
    private lateinit var radioGroupEstrategias: RadioGroup // Asumiremos que añades un RadioGroup en tu XML

  /*
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

        // Obtener token desde recursos
        MAPBOX_ACCESS_TOKEN = getString(R.string.mapbox_access_token)

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            val annotationApi = mapView.annotations
            lineAnnotationManager = annotationApi.createPolylineAnnotationManager()
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            cargarMarcadoresSurtidores()

            mapView.gestures.addOnMapClickListener { point ->
                if (surtidorSeleccionado != null) {
                    val origen = Point.fromLngLat(surtidorSeleccionado!!.longitud, surtidorSeleccionado!!.latitud)
                    val destino = point

                    // Obtener la ruta usando Mapbox Directions API
                    obtenerRuta(origen, destino)
                } else {
                    Toast.makeText(this, "Primero selecciona un surtidor", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }

        cargarTiposCombustible()
        btnCalcular.setOnClickListener { realizarCalculo() }
    }
    */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_probabilidad_abastecimiento) // Asegúrate que tu layout tiene el RadioGroup

        // --- Inicialización de variables existentes ---
        mapView = findViewById(R.id.mapView)
        spinnerTipo = findViewById(R.id.spinnerTipoCombustible)
        btnCalcular = findViewById(R.id.btnCalcular)
        txtDistancia = findViewById(R.id.txtDistancia)
        radioGroupEstrategias = findViewById(R.id.radioGroupEstrategias) // **Añade este RadioGroup a tu XML**

        nSurtidor = NSurtidor(this)
        nTipo = NTipoCombustible(this)
        nStock = NStockCombustible(this)
        MAPBOX_ACCESS_TOKEN = getString(R.string.mapbox_access_token)

        // --- Configuración inicial de la Estrategia ---
        // Establecer la estrategia por defecto (puedes cambiarla)
        currentStrategy = CalculoTiempoEsperaStrategy()


        setupMap() // Refactorizar configuración del mapa a un método
        cargarTiposCombustible()

        // --- Listener para el RadioGroup para cambiar de estrategia ---
        radioGroupEstrategias.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbTiempoEspera -> {
                    currentStrategy = CalculoTiempoEsperaStrategy()
                    Toast.makeText(this, "Estrategia: Tiempo de Espera", Toast.LENGTH_SHORT).show()
                }
                R.id.rbDistanciaSimple -> {
                    currentStrategy = CalculoSimpleDistanciaStrategy()
                    Toast.makeText(this, "Estrategia: Distancia Simple", Toast.LENGTH_SHORT).show()
                }
                R.id.rbIndiceConveniencia -> {
                    currentStrategy = CalculoRelacionStockDistanciaBombasStrategy()
                    Toast.makeText(this, "Estrategia: Índice Conveniencia", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCalcular.setOnClickListener { realizarCalculoConEstrategia() }
    }

    /*

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
            mapaSurtidores[surtidor.nombre] = surtidor
        }

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

    private fun obtenerRuta(origen: Point, destino: Point) {
        // Usar corrutinas para llamada de red
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = construirUrlDireciones(origen, destino)
                val response = realizarLlamadaHTTP(url)
                val rutaPuntos = parsearRespuestaRuta(response)

                withContext(Dispatchers.Main) {
                    if (rutaPuntos.isNotEmpty()) {
                        routePoints = rutaPuntos.toMutableList()
                        dibujarRuta(routePoints)
                        rutaDistancia = calcularDistanciaRuta(routePoints)
                        txtDistancia.text = "Total ${rutaDistancia.toInt()} mts"
                    } else {
                        Toast.makeText(this@CalcularProbabilidadAbastecimientoActivity,
                            "No se pudo obtener la ruta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Routing", "Error obteniendo ruta: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CalcularProbabilidadAbastecimientoActivity,
                        "Error al obtener la ruta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun construirUrlDireciones(origen: Point, destino: Point): String {
        return "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                "${origen.longitude()},${origen.latitude()};" +
                "${destino.longitude()},${destino.latitude()}" +
                "?geometries=geojson&access_token=$MAPBOX_ACCESS_TOKEN"
    }

    private fun realizarLlamadaHTTP(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()
            return response
        } else {
            throw Exception("HTTP Error: $responseCode")
        }
    }

    private fun parsearRespuestaRuta(jsonResponse: String): List<Point> {
        try {
            val jsonObject = JSONObject(jsonResponse)
            val routes = jsonObject.getJSONArray("routes")

            if (routes.length() > 0) {
                val route = routes.getJSONObject(0)
                val geometry = route.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")

                val points = mutableListOf<Point>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    val lng = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    points.add(Point.fromLngLat(lng, lat))
                }
                return points
            }
        } catch (e: Exception) {
            Log.e("Parsing", "Error parseando respuesta: ${e.message}")
        }
        return emptyList()
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

    private fun realizarCalculo() {
        if (routePoints.size < 2 || surtidorSeleccionado == null) {
            Toast.makeText(this, "Dibuja la distancia desde un surtidor seleccionado", Toast.LENGTH_SHORT).show()
            return
        }

        val distancia = rutaDistancia
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

     */


    // cargarTiposCombustible, cargarMarcadoresSurtidores, obtenerRuta, construirUrlDireciones,
    // realizarLlamadaHTTP, parsearRespuestaRuta, dibujarRuta, calcularDistanciaRuta
    // ... (estos métodos permanecen igual, solo asegúrate que `calcularDistanciaRuta` actualice `rutaDistancia`)

    /**
     * Configura el MapView, incluyendo la carga del estilo, inicialización de los administradores
     * de anotaciones y la configuración de listeners para clics en el mapa.
     */
    private fun setupMap() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            // Es una buena práctica usar el 'style' que se pasa en el lambda
            // para asegurarte de que el estilo está completamente cargado.
            val annotationApi = mapView.annotations
            lineAnnotationManager = annotationApi.createPolylineAnnotationManager()
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            cargarMarcadoresSurtidores() // Cargar los marcadores una vez que el mapa esté listo

            mapView.gestures.addOnMapClickListener { point ->
                if (surtidorSeleccionado != null) {
                    val origen = Point.fromLngLat(surtidorSeleccionado!!.longitud, surtidorSeleccionado!!.latitud)
                    val destino = point // El destino es donde el usuario hace clic

                    // Antes de obtener una nueva ruta, limpia la anterior visualmente
                    lineAnnotationManager.deleteAll()
                    routePoints.clear()
                    txtDistancia.text = "Calculando ruta..." // Feedback al usuario

                    obtenerRuta(origen, destino)
                } else {
                    Toast.makeText(this, "Primero selecciona un surtidor del mapa", Toast.LENGTH_SHORT).show()
                }
                true // Indica que el evento de clic ha sido manejado
            }
        }
    }

    /**
     * Realiza el cálculo de probabilidad utilizando la estrategia actualmente seleccionada.
     */
    private fun realizarCalculoConEstrategia() {
        // Validación: se necesita un surtidor seleccionado y una ruta dibujada (para la distancia)
        if (surtidorSeleccionado == null) {
            Toast.makeText(this, "Por favor, selecciona un surtidor del mapa primero.", Toast.LENGTH_LONG).show()
            return
        }
        if (routePoints.size < 2) { // routePoints se llena después de obtenerRuta
            Toast.makeText(this, "Por favor, haz clic en el mapa para definir tu destino y calcular la ruta desde el surtidor.", Toast.LENGTH_LONG).show()
            return
        }

        val tipoSeleccionadoEntity = tiposCombustible[spinnerTipo.selectedItemPosition]

        // Obtener el stock para el surtidor y tipo de combustible seleccionados
        val stockDelTipoSeleccionado = nStock.obtenerPorSurtidor(surtidorSeleccionado!!.id)
            .firstOrNull { it.idTipoCombustible == tipoSeleccionadoEntity.id }

        // La variable rutaDistancia ya debería estar actualizada por calcularDistanciaRuta()
        // que es llamada dentro de obtenerRuta()

        // --- Delegar el cálculo a la estrategia actual ---
        val mensajeResultado = currentStrategy.calcular(
            surtidor = surtidorSeleccionado!!,
            stock = stockDelTipoSeleccionado, // Puede ser null
            distanciaEnMetros = rutaDistancia, // Esta es la distancia de la ruta dibujada
            tipoCombustibleNombre = tipoSeleccionadoEntity.nombre
        )
        // --- Fin de la delegación ---

        mostrarResultado(mensajeResultado)
    }

    private fun mostrarResultado(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Resultado del Cálculo de Conveniencia") // Título más genérico
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    // ... (resto de tus métodos: cargarTiposCombustible, cargarMarcadoresSurtidores, etc. sin cambios)
    // Asegúrate de que los métodos para dibujar la ruta y calcular la distancia se llamen correctamente
    // y que `rutaDistancia` se actualice.

    private fun cargarTiposCombustible() {
        tiposCombustible = nTipo.obtenerTodos()
        val nombres = tiposCombustible.map { it.nombre }
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombres)
    }

    private fun cargarMarcadoresSurtidores() {
        pointAnnotationManager.deleteAll()
        val surtidores = nSurtidor.obtenerTodos()
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
        // Redimensionar el bitmap para que sea más pequeño
        val resizedBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            (originalBitmap.width * 0.07).toInt(), //Ajusta según necesidad
            (originalBitmap.height * 0.07).toInt(), //Ajusta según necesidad
            true
        )

        val mapaSurtidores = mutableMapOf<String, Surtidor>() // Para mapear TextField a Surtidor

        for (surtidor in surtidores) {
            val annotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(surtidor.longitud, surtidor.latitud))
                .withIconImage(resizedBitmap) // Usar el bitmap redimensionado
                .withTextField(surtidor.nombre) // Usar el nombre como identificador de texto

            val annotation = pointAnnotationManager.create(annotationOptions)
            mapaSurtidores[surtidor.nombre] = surtidor // Llenar el mapa
        }

        // Listener para clics en marcadores
        pointAnnotationManager.addClickListener { clickedAnnotation ->
            // Obtener el nombre del surtidor del TextField de la anotación
            val nombreSurtidorClickeado = clickedAnnotation.textField
            if (nombreSurtidorClickeado != null) {
                val surtidor = mapaSurtidores[nombreSurtidorClickeado]
                if (surtidor != null) {
                    surtidorSeleccionado = surtidor
                    Toast.makeText(this, "Surtidor seleccionado: ${surtidor.nombre}", Toast.LENGTH_SHORT).show()
                    // Opcional: limpiar ruta anterior si se selecciona un nuevo surtidor
                    lineAnnotationManager.deleteAll()
                    routePoints.clear()
                    txtDistancia.text = "Distancia: (haz clic en el mapa para destino)"
                    return@addClickListener true // Evento consumido
                }
            }
            false // Evento no consumido
        }
    }


    private fun obtenerRuta(origen: Point, destino: Point) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = construirUrlDireciones(origen, destino)
                val response = realizarLlamadaHTTP(url)
                val rutaPuntosCalculados = parsearRespuestaRuta(response)

                withContext(Dispatchers.Main) {
                    if (rutaPuntosCalculados.isNotEmpty()) {
                        routePoints.clear() // Limpiar puntos anteriores
                        routePoints.addAll(rutaPuntosCalculados) // Añadir nuevos puntos
                        dibujarRuta(routePoints)
                        rutaDistancia = calcularDistanciaRuta(routePoints) // Actualizar la distancia global
                        txtDistancia.text = "Distancia de ruta: ${"%.0f".format(rutaDistancia)} mts"
                    } else {
                        Toast.makeText(this@CalcularProbabilidadAbastecimientoActivity,
                            "No se pudo obtener la ruta.", Toast.LENGTH_SHORT).show()
                        txtDistancia.text = "Distancia: (Error al obtener ruta)"
                    }
                }
            } catch (e: Exception) {
                Log.e("RoutingError", "Error obteniendo ruta: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CalcularProbabilidadAbastecimientoActivity,
                        "Error al obtener la ruta: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    txtDistancia.text = "Distancia: (Error de conexión)"
                }
            }
        }
    }


    private fun construirUrlDireciones(origen: Point, destino: Point): String {
        // Asegúrate que MAPBOX_ACCESS_TOKEN está inicializado y es válido
        return "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                "${origen.longitude()},${origen.latitude()};" +
                "${destino.longitude()},${destino.latitude()}" +
                "?alternatives=false&geometries=geojson&overview=full&steps=false" + // overview=full para más puntos si es necesario
                "&access_token=$MAPBOX_ACCESS_TOKEN"
    }

    private fun realizarLlamadaHTTP(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        var result = ""
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000 // 10 segundos
            connection.readTimeout = 10000   // 10 segundos

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorResult = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error desconocido"
                Log.e("HTTPError", "HTTP Error Code: $responseCode, Message: $errorResult, URL: $urlString")
                throw Exception("HTTP Error: $responseCode. $errorResult")
            }
        } finally {
            connection.disconnect()
        }
        return result
    }

    private fun parsearRespuestaRuta(jsonResponse: String): List<Point> {
        val points = mutableListOf<Point>()
        try {
            val jsonObject = JSONObject(jsonResponse)
            val routes = jsonObject.optJSONArray("routes") // Usar optJSONArray para manejar nulidad
            if (routes != null && routes.length() > 0) {
                val route = routes.getJSONObject(0) // Tomamos la primera ruta
                val geometry = route.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")

                for (i in 0 until coordinates.length()) {
                    val coordArray = coordinates.getJSONArray(i)
                    val lng = coordArray.getDouble(0)
                    val lat = coordArray.getDouble(1)
                    points.add(Point.fromLngLat(lng, lat))
                }
            } else {
                Log.w("ParsingWarning", "No se encontraron rutas en la respuesta JSON: $jsonResponse")
            }
        } catch (e: Exception) {
            Log.e("ParsingError", "Error parseando la respuesta de la ruta: ${e.message}", e)
        }
        return points
    }


    private fun dibujarRuta(puntos: List<Point>) {
        lineAnnotationManager.deleteAll() // Limpiar rutas anteriores
        if (puntos.size >= 2) {
            val polylineAnnotationOptions = PolylineAnnotationOptions()
                .withPoints(puntos)
                .withLineColor("#FF0000") // Rojo
                .withLineWidth(5.0)
            lineAnnotationManager.create(polylineAnnotationOptions)
        }
    }

    private fun calcularDistanciaRuta(puntos: List<Point>): Double {
        if (puntos.size < 2) return 0.0
        var distanciaTotal = 0.0
        for (i in 0 until puntos.size - 1) {
            val resultados = FloatArray(1) // Array para almacenar el resultado
            Location.distanceBetween(
                puntos[i].latitude(), puntos[i].longitude(),
                puntos[i + 1].latitude(), puntos[i + 1].longitude(),
                resultados
            )
            distanciaTotal += resultados[0] // El resultado se almacena en resultados[0]
        }
        return distanciaTotal // Distancia en metros
    }

}
