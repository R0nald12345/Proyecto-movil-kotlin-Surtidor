package com.example.parcialproyectosurtidor.firstapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.parcialproyectosurtidor.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Inicializar y configurar el MapView
        mapView = findViewById(R.id.mapView)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-70.0162, -15.8402)) // Coordenadas para Perú o ajusta según necesites
                .zoom(12.0)
                .build()
        )

        // Configurar botones del menú
        findViewById<Button>(R.id.btn_inicio).setOnClickListener {
            Toast.makeText(this, "Inicio seleccionado", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_agregar_surtidor).setOnClickListener {
            Toast.makeText(this, "Agregar Surtidor seleccionado", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawers()
            // Implementa la navegación o acción necesaria
        }

        findViewById<Button>(R.id.btn_surtidor_sercano).setOnClickListener {
            Toast.makeText(this, "Surtidor Sercano seleccionado", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawers()
            // Implementa la navegación o acción necesaria
        }

        findViewById<Button>(R.id.btn_salir).setOnClickListener {
            finish() // Cierra la aplicación
        }

        // Configurar un botón o método para abrir el drawer
        // En tu caso, podrías necesitar añadir un botón en la vista del mapa

    }


    // Si quieres añadir un botón en la barra de herramientas para abrir el drawer
    /*private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar)) // Necesitarías añadir un Toolbar en tu layout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, findViewById(R.id.toolbar),
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }*/
}