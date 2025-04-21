package com.example.parcialproyectosurtidor.presentacion


import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.ceil

class CalcularProbabilidadAbastecimiento : AppCompatActivity() {

    private lateinit var spinnerSurtidores: Spinner
    private lateinit var btnUbicacionActual: Button
    private lateinit var btnCalcular: Button
    private lateinit var nSurtidor: NSurtidor
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var surtidoresLista: List<Surtidor> = listOf()
    private var selectedSurtidor: Surtidor? = null
    private var userLocation: Location? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_probabilidad_abastecimiento)

        // Inicializo capa de negocio
        nSurtidor = NSurtidor(this)

        // Inicializo el cliente de ubicación de Google
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializo vistas
        spinnerSurtidores = findViewById(R.id.spinnerSurtidores)
        btnUbicacionActual = findViewById(R.id.btnUbicacionActual)
        btnCalcular = findViewById(R.id.btnCalcular)

        // Cargar lista de surtidores
        cargarSurtidores()

        // Configurar listeners
        btnUbicacionActual.setOnClickListener {
            checkLocationPermission()
        }

        btnCalcular.setOnClickListener {
            if (selectedSurtidor == null) {
                Toast.makeText(this, "Por favor seleccione un surtidor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mostrarDialogDatosCalculos()
        }

        spinnerSurtidores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSurtidor = surtidoresLista[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSurtidor = null
            }
        }
    }

    private fun cargarSurtidores() {
        // Obtener surtidores de la capa de negocio
        surtidoresLista = nSurtidor.getSurtidores()

        // Crear adaptador para el spinner
        val surtidoresNombres = surtidoresLista.map { it.nombre }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, surtidoresNombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar adaptador al spinner
        spinnerSurtidores.adapter = adapter
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso no concedido, solicitarlo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permiso ya concedido, obtener ubicación
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Guardar la ubicación actual
                    userLocation = it
                    Toast.makeText(
                        this,
                        "Ubicación actual obtenida: ${it.latitude}, ${it.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnUbicacionActual.text = "Ubicación actual disponible"
                    btnUbicacionActual.isEnabled = false
                } ?: run {
                    Toast.makeText(
                        this,
                        "No se pudo obtener la ubicación. Asegúrate de tener el GPS activado.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al obtener ubicación: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun mostrarDialogDatosCalculos() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_calcular_probabilidad, null)

        val etCantidadCombustible = dialogView.findViewById<EditText>(R.id.etCantidadCombustible)
        val etCantidadAutos = dialogView.findViewById<EditText>(R.id.etCantidadAutos)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Datos adicionales")
            .setView(dialogView)
            .setPositiveButton("Calcular") { _, _ ->
                try {
                    val cantidadCombustible = etCantidadCombustible.text.toString().toDouble()
                    val cantidadAutos = etCantidadAutos.text.toString().toInt()

                    calcularProbabilidad(cantidadCombustible, cantidadAutos)
                } catch (e: Exception) {
                    Toast.makeText(this, "Por favor ingrese valores válidos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun calcularProbabilidad(cantidadCombustible: Double, cantidadAutos: Int) {
        if (selectedSurtidor == null || userLocation == null) {
            Toast.makeText(this, "Faltan datos para el cálculo", Toast.LENGTH_SHORT).show()
            return
        }

        val combustiblePorAuto = 45.0
        val tiempoPorAuto = 5.0
        val cantidadBombas = selectedSurtidor!!.cantidadBombas
        val autosSimultaneos = cantidadBombas * 2

        val distanciaKm = calcularDistancia(
            userLocation!!.latitude, userLocation!!.longitude,
            selectedSurtidor!!.latitud, selectedSurtidor!!.longitud
        )

        val combustibleNecesario = (cantidadAutos + 1) * combustiblePorAuto
        val alcanzaCombustible = cantidadCombustible >= combustibleNecesario

        val tandas = ceil(cantidadAutos.toDouble() / autosSimultaneos)
        val tiempoEsperaMinutos = tandas * tiempoPorAuto

        val mensaje = StringBuilder()
        mensaje.append("Surtidor seleccionado: ${selectedSurtidor!!.nombre}\n")
        mensaje.append("Cantidad de bombas: $cantidadBombas (capacidad: $autosSimultaneos autos simultáneos)\n\n")
        mensaje.append("Distancia al surtidor: ${String.format("%.2f", distanciaKm)} km\n\n")
        mensaje.append("Tiempo estimado de espera: ${tiempoEsperaMinutos.toInt()} minutos\n\n")

        if (alcanzaCombustible) {
            mensaje.append("🔋 PROBABILIDAD ALTA: El combustible disponible (${cantidadCombustible} L) es suficiente para todos los autos en espera y el suyo.")
        } else {
            val autosQueAlcanzan = (cantidadCombustible / combustiblePorAuto).toInt()
            if (autosQueAlcanzan >= cantidadAutos) {
                mensaje.append("⚠️ PROBABILIDAD MEDIA: El combustible alcanzará para usted, pero quedará poco para los siguientes.")
            } else {
                mensaje.append("❌ PROBABILIDAD BAJA: El combustible probablemente NO alcance para su auto. Solo alcanza para aproximadamente $autosQueAlcanzan de los $cantidadAutos autos en espera.")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Resultado del cálculo")
            .setMessage(mensaje.toString())
            .setPositiveButton("Aceptar", null)
            .show()
    }


    private fun calcularDistancia(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val radioTierra = 6371.0 // Radio de la Tierra en km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return radioTierra * c // Distancia en km
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                    getLastLocation()
                } else {
                    // Permiso denegado
                    Toast.makeText(
                        this,
                        "No se concedieron los permisos de ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
}