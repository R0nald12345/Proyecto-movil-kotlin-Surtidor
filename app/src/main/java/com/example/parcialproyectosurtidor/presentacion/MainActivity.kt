package com.example.parcialproyectosurtidor.presentacion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.example.parcialproyectosurtidor.presentacion.Surtidor.GestionarSurtidoresActivity
import com.example.parcialproyectosurtidor.presentacion.Surtidor.SurtidorCercanoActivity
import com.example.parcialproyectosurtidor.presentacion.TipoCombustible.GestionarTiposCombustibleActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nSurtidor: NSurtidor
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationPoint: Point? = null
    private var userLocationMarker: com.mapbox.maps.plugin.annotation.generated.PointAnnotation? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar capa de negocio
        nSurtidor = NSurtidor(this)

        // Inicializar el cliente de ubicación de Google
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializar el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Inicializar y configurar el MapView
        mapView = findViewById(R.id.mapView)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-63.1821, -17.7832)) // Santa Cruz, Bolivia
                .zoom(12.0)
                .build()
        )

        // Inicializar el pointAnnotationManager
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

        // Agregar marcadores de surtidores
        cargarMarcadoresSurtidores()

        // Configurar el botón de ubicación
        findViewById<FloatingActionButton>(R.id.fab_location).setOnClickListener {
            checkLocationPermission()
        }

        // Configurar botones del menú
        findViewById<Button>(R.id.btn_inicio).setOnClickListener {
            Toast.makeText(this, "Inicio seleccionado", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawers()
        }


        findViewById<Button>(R.id.btn_tipos_combustibles).setOnClickListener {
            val intent = Intent(this, GestionarTiposCombustibleActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }


        findViewById<Button>(R.id.btn_surtidor_sercano).setOnClickListener {
            val intent = Intent(this, SurtidorCercanoActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }


        findViewById<Button>(R.id.btn_listado_surtidores).setOnClickListener {
            val intent = Intent(this, GestionarSurtidoresActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_calcular_probabilidad).setOnClickListener {
            val intent = Intent(this, CalcularProbabilidadAbastecimientoActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }


        findViewById<Button>(R.id.btn_salir).setOnClickListener {
            finish() // Cierra la aplicación
        }


    }

    // Verifica el permiso de ubicación
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

    // Obtiene la última ubicación del usuario
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
                    userLocationPoint = Point.fromLngLat(it.longitude, it.latitude)

                    // Mostrar la ubicación en el mapa
                    showUserLocation(it)
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

    // Muestra la ubicación del usuario en el mapa
    private fun showUserLocation(location: Location) {
        // Mover cámara a la ubicación del usuario
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(location.longitude, location.latitude))
                .zoom(15.0)
                .build()
        )

        // Eliminar el marcador anterior si existe
        userLocationMarker?.let {
            pointAnnotationManager.delete(it)
        }

        // Crear un marcador azul para la ubicación del usuario
        val originalBitmap = BitmapFactory.decodeResource(
            resources, R.drawable.blue_marker // Debes añadir este recurso
        )

        // Redimensionar el bitmap para que sea más pequeño
        val resizedBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            (originalBitmap.width * 0.07).toInt(),
            (originalBitmap.height * 0.07).toInt(),
            true
        )

        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(location.longitude, location.latitude))
            .withIconImage(resizedBitmap)
            .withTextField("Mi ubicación")

        // Añadir el marcador al mapa
        userLocationMarker = pointAnnotationManager.create(pointAnnotationOptions)
    }

    // Cargar marcadores de surtidores
    private fun cargarMarcadoresSurtidores() {
        // Obtener lista de surtidores desde la capa de negocio
        val surtidores = nSurtidor.obtenerTodos()

        // Limpiar marcadores existentes (excepto el de ubicación del usuario)
        val annotations = pointAnnotationManager.annotations
        for (annotation in annotations) {
            if (annotation != userLocationMarker) {
                pointAnnotationManager.delete(annotation)
            }
        }

        // Agregar un marcador para cada surtidor
        for (surtidor in surtidores) {
            // Convertir recurso drawable a bitmap
            val originalBitmap = BitmapFactory.decodeResource(
                resources, R.drawable.red_marker
            )

            // Redimensionar el bitmap para que sea más pequeño
            val resizedBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * 0.07).toInt(),
                (originalBitmap.height * 0.07).toInt(),
                true
            )

            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(surtidor.longitud, surtidor.latitud))
                .withIconImage(resizedBitmap)
                .withTextField(surtidor.nombre)

            // Agregar la anotación al mapa
            pointAnnotationManager.create(pointAnnotationOptions)
        }
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

    override fun onResume() {
        super.onResume()
        // Recargo los marcadores cuando la actividad vuelve a primer plano
        cargarMarcadoresSurtidores()
    }
}
