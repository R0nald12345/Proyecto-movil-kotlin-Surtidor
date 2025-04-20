package com.example.parcialproyectosurtidor.presentacion

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SurtidorSercanoActivity : AppCompatActivity() {

    private lateinit var nSurtidor: NSurtidor
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surtidor_sercano)

        nSurtidor = NSurtidor(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        obtenerUbicacionYMostrarSurtidorMasCercano()
    }

    private fun obtenerUbicacionYMostrarSurtidorMasCercano() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
                    val surtidor = nSurtidor.getSurtidorMasCercano(
                        it.latitude, it.longitude
                    )
                    if (surtidor != null) {
                        // Puedes mostrarlo en un TextView también
                        Toast.makeText(
                            this,
                            "Surtidor más cercano: ${surtidor.nombre}",
                            Toast.LENGTH_LONG
                        ).show()
                        findViewById<TextView>(R.id.txtResultado)?.text =
                            "Más cercano:\n${surtidor.nombre}\nLat: ${surtidor.latitud}, Lng: ${surtidor.longitud}"
                    } else {
                        Toast.makeText(this, "No se encontraron surtidores", Toast.LENGTH_SHORT)
                            .show()
                    }
                } ?: Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacionYMostrarSurtidorMasCercano()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
