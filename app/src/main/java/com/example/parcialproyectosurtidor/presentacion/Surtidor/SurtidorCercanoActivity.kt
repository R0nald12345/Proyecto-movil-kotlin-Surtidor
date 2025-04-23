package com.example.parcialproyectosurtidor.presentacion.Surtidor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.negocio.NSurtidor
//import com.example.parcialproyectosurtidor.presentacion.CalcularProbabilidadAbastecimiento
import com.example.parcialproyectosurtidor.presentacion.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SurtidorCercanoActivity : AppCompatActivity() {

    private lateinit var nSurtidor: NSurtidor

    private lateinit var mapView: MapView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var txtDistancia: TextView


    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surtidor_cercano)

        mapView = findViewById(R.id.mapView)
        txtDistancia = findViewById(R.id.txtDistancia)
        drawerLayout = findViewById(R.id.drawer_layout)

        nSurtidor = NSurtidor(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

        obtenerUbicacionYMostrarRuta()

        // ---------------- MENÚ LATERAL ----------------
        findViewById<Button>(R.id.btn_inicio).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            drawerLayout.closeDrawers()
        }


        findViewById<Button>(R.id.btn_surtidor_sercano).setOnClickListener {
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_listado_surtidores).setOnClickListener {
            startActivity(Intent(this, GestionarSurtidoresActivity::class.java))
            drawerLayout.closeDrawers()
        }

        /*
        findViewById<Button>(R.id.btn_calcular_probabilidad).setOnClickListener {
            startActivity(Intent(this, CalcularProbabilidadAbastecimiento::class.java))
            drawerLayout.closeDrawers()
        }
        */


        findViewById<Button>(R.id.btn_salir).setOnClickListener {
            finishAffinity()
        }

        // ---------- CAMBIAR COLOR DEL BOTÓN ACTIVO ----------
        val activeColor = ContextCompat.getColor(this, R.color.purple_500)
        val defaultColor = Color.TRANSPARENT

        val btnInicio = findViewById<Button>(R.id.btn_inicio)

        val btnSercano = findViewById<Button>(R.id.btn_surtidor_sercano)
        val btnListado = findViewById<Button>(R.id.btn_listado_surtidores)
        val btnProbabilidad = findViewById<Button>(R.id.btn_calcular_probabilidad)

        // Reiniciar todos los colores
        btnInicio.setBackgroundColor(defaultColor)

        btnSercano.setBackgroundColor(activeColor)
        btnListado.setBackgroundColor(defaultColor)
        btnProbabilidad.setBackgroundColor(defaultColor)

        // Opcional: cambiar color del texto también
        val white = ContextCompat.getColor(this, android.R.color.white)
        val black = ContextCompat.getColor(this, android.R.color.black)

        btnInicio.setTextColor(white)

        btnSercano.setTextColor(white)
        btnListado.setTextColor(white)
        btnProbabilidad.setTextColor(white)
    }

    private fun obtenerUbicacionYMostrarRuta() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val userPoint = Point.fromLngLat(it.longitude, it.latitude)
                    val surtidor = nSurtidor.getSurtidorMasCercano(it.latitude, it.longitude)

                    if (surtidor != null) {
                        val surtidorPoint = Point.fromLngLat(surtidor.longitud, surtidor.latitud)

                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(userPoint)
                                .zoom(13.5)
                                .build()
                        )

                        val blueMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.blue_marker)
                        val redMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.red_marker)

                        val userMarker = PointAnnotationOptions()
                            .withPoint(userPoint)
                            .withIconImage(
                                Bitmap.createScaledBitmap(
                                blueMarkerBitmap, (blueMarkerBitmap.width * 0.07).toInt(),
                                (blueMarkerBitmap.height * 0.07).toInt(), true
                            ))
                            .withTextField("Tú")

                        val surtidorMarker = PointAnnotationOptions()
                            .withPoint(surtidorPoint)
                            .withIconImage(
                                Bitmap.createScaledBitmap(
                                redMarkerBitmap, (redMarkerBitmap.width * 0.07).toInt(),
                                (redMarkerBitmap.height * 0.07).toInt(), true
                            ))
                            .withTextField(surtidor.nombre)

                        pointAnnotationManager.create(userMarker)
                        pointAnnotationManager.create(surtidorMarker)

                        val polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
                        val line = PolylineAnnotationOptions()
                            .withPoints(listOf(userPoint, surtidorPoint))
                            .withLineColor("#FF0000")
                            .withLineWidth(4.0)
                        polylineAnnotationManager.create(line)

                        val distancia = calcularDistancia(
                            it.latitude, it.longitude,
                            surtidor.latitud, surtidor.longitud
                        )

                        txtDistancia.text = "Distancia: %.2f km".format(distancia)
                    } else {
                        Toast.makeText(this, "No se encontró surtidor cercano", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radioTierra * c
    }

    //extensión para la clase Double.  Añade una nueva función (pow)
    // a todos los objetos Double, que calcula la potencia de un número
    private fun Double.pow(exp: Double): Double = Math.pow(this, exp)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionYMostrarRuta()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}