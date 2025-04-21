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

class GestionarSurtidores : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var nSurtidor: NSurtidor
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_surtidores)

        // Inicializo la capa de negocio
        nSurtidor = NSurtidor(this)

        // Inicializo el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Configuro RecyclerView
        recyclerView = findViewById(R.id.rvSurtidores)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Cargo los datos
        cargarSurtidores()

        // Configuro botones del menú
        findViewById<Button>(R.id.btn_inicio).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_agregar_en_mapa).setOnClickListener {
            val intent = Intent(this, AgregarSurtidorEnMapaActivity::class.java)
            startActivity(intent)
        }



        findViewById<Button>(R.id.btn_surtidor_sercano).setOnClickListener {

            Toast.makeText(this, "Surtidor Cercano seleccionado", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_listado_surtidores).setOnClickListener {

            drawerLayout.closeDrawers()
        }

        findViewById<Button>(R.id.btn_salir).setOnClickListener {
            // Cierro todas las actividades y la aplicación
            finishAffinity()
        }

        findViewById<Button>(R.id.btn_ver_en_mapa).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun cargarSurtidores() {
        val surtidores = nSurtidor.getSurtidores()

        val adapter = SurtidorAdapter(
            surtidores,
            // Listener para editar
            { surtidor ->
                // Aquí puedo implementar la lógica para editar un surtidor

                    val intent = Intent(this, EditarSurtidorActivity::class.java)
                    intent.putExtra("id", surtidor.id)
                    startActivity(intent)

                Toast.makeText(this, "Editar: ${surtidor.nombre}", Toast.LENGTH_SHORT).show()
                // TODO: Implementar la navegación a la actividad para editar
            },
            //tengo el id y el nombre del surtidor Listener para eliminar
            { surtidor ->
                mostrarDialogoConfirmacionEliminar(surtidor.id, surtidor.nombre)
            }
        )

        recyclerView.adapter = adapter
    }

    // Método para mostrar el diálogo de confirmación de eliminación
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