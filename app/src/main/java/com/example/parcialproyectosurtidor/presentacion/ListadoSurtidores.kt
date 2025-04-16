package com.example.parcialproyectosurtidor.presentacion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialproyectosurtidor.R
import android.widget.Button
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AlertDialog
import com.example.parcialproyectosurtidor.presentacion.adapter.SurtidorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListadoSurtidores : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var nSurtidor: NSurtidor
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_surtidores)

        // Inicializar la capa de negocio
        nSurtidor = NSurtidor(this)

        // Inicializar el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.rvSurtidores)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar los datos
        cargarSurtidores()

        // Configurar botones del menú
        findViewById<Button>(R.id.btn_inicio).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_agregar_surtidor).setOnClickListener {
            val intent = Intent(this, AgregarSurtidorActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_surtidor_sercano).setOnClickListener {
            // Si tienes implementada esta actividad
            // val intent = Intent(this, SurtidorCercanoActivity::class.java)
            // startActivity(intent)
            Toast.makeText(this, "Surtidor Cercano seleccionado", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_listado_surtidores).setOnClickListener {
            // Ya estamos en esta actividad, solo cerramos el drawer
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_salir).setOnClickListener {
            // Cierra todas las actividades y la aplicación
            finishAffinity()
        }
    }

    private fun cargarSurtidores() {
        val surtidores = nSurtidor.getSurtidores()

        val adapter = SurtidorAdapter(
            surtidores,
            // Listener para editar
            { surtidor ->
                // Aquí puedes implementar la lógica para editar un surtidor
                Toast.makeText(this, "Editar: ${surtidor.nombre}", Toast.LENGTH_SHORT).show()
                // TODO: Implementar la navegación a la actividad para editar
            },
            // Listener para eliminar
            { surtidor ->
                mostrarDialogoConfirmacionEliminar(surtidor.id, surtidor.nombre)
            }
        )

        recyclerView.adapter = adapter
    }

    private fun mostrarDialogoConfirmacionEliminar(idSurtidor: Int, nombreSurtidor: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro que deseas eliminar el surtidor '$nombreSurtidor'?")
            .setPositiveButton("Eliminar") { _, _ ->
                // Eliminar el surtidor
                if (nSurtidor.eliminar(idSurtidor)) {
                    Toast.makeText(this, "Surtidor eliminado correctamente", Toast.LENGTH_SHORT).show()
                    // Recargar la lista
                    cargarSurtidores()
                } else {
                    Toast.makeText(this, "Error al eliminar el surtidor", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Recargar los datos cuando la actividad vuelve a primer plano
        cargarSurtidores()
    }
}