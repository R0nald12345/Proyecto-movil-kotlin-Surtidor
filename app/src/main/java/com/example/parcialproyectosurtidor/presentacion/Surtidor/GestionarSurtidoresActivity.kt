package com.example.parcialproyectosurtidor.presentacion.Surtidor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.negocio.NSurtidor
import com.example.parcialproyectosurtidor.negocio.NStockCombustible
import com.example.parcialproyectosurtidor.negocio.NTipoCombustible

class GestionarSurtidoresActivity : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var nSurtidor: NSurtidor
    private lateinit var nStockCombustible: NStockCombustible
    private lateinit var nTipoCombustible: NTipoCombustible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_surtidores)

        // Inicializo la capa de negocio
        nSurtidor = NSurtidor(this)
        nStockCombustible = NStockCombustible(this)
        nTipoCombustible = NTipoCombustible(this)

        // Configuro el contenedor LinearLayout
        linearLayout = findViewById(R.id.linear_layout_surtidores)

        // Cargo los datos de surtidores
        cargarSurtidores()

        // Botón para agregar un surtidor
        findViewById<Button>(R.id.btn_agregar_surtidor).setOnClickListener {
            // Llamamos al Activity para agregar un nuevo surtidor
            val intent = Intent(this, AgregarSurtidorActivity::class.java)
            startActivity(intent)
        }
    }

    // Método para cargar los surtidores en el LinearLayout
    private fun cargarSurtidores() {
        val surtidores = nSurtidor.obtenerTodos()
        linearLayout.removeAllViews() // Limpiar el LinearLayout

        val inflater = LayoutInflater.from(this)

        for (surtidor in surtidores) {
            // Inflar la vista del item_surtidor para cada surtidor
            val itemView = inflater.inflate(R.layout.item_surtidor, linearLayout, false)

            // Configurar el nombre del surtidor
            val tvNombreSurtidor = itemView.findViewById<TextView>(R.id.tvNombreSurtidor)
            tvNombreSurtidor.text = surtidor.nombre

            // Configurar los botones
            val btnEditar = itemView.findViewById<Button>(R.id.btnEditar)
            val btnVer = itemView.findViewById<Button>(R.id.btnVer)
            val btnEliminar = itemView.findViewById<Button>(R.id.btnEliminar)

            btnEditar.setOnClickListener {
                mostrarDialogoEditar(surtidor)
            }

            btnVer.setOnClickListener {
                // Aquí puedes mostrar detalles del surtidor, como el stock de combustible
                mostrarDetallesSurtidor(surtidor)
            }

            btnEliminar.setOnClickListener {
                mostrarDialogoConfirmacionEliminar(surtidor.id, surtidor.nombre)
            }

            // Agregar la vista al contenedor
            linearLayout.addView(itemView)
        }
    }

    // Método para mostrar detalles del surtidor (como el stock)
    private fun mostrarDetallesSurtidor(surtidor: Surtidor) {
        val stockCombustibles = nStockCombustible.obtenerPorSurtidor(surtidor.id)
        val tiposCombustible = stockCombustibles.map { stock ->
            val tipo = nTipoCombustible.obtenerPorId(stock.idTipoCombustible)
            "${tipo?.nombre ?: "Desconocido"}: ${stock.cantidad} litros"
        }

        AlertDialog.Builder(this)
            .setTitle("Detalles de ${surtidor.nombre}")
            .setMessage("Stock de combustible:\n${tiposCombustible.joinToString("\n")}")
            .setPositiveButton("Cerrar", null)
            .show()
    }

    // Método para mostrar el diálogo de confirmación de eliminación
    private fun mostrarDialogoConfirmacionEliminar(idSurtidor: Int, nombreSurtidor: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro que deseas eliminar el surtidor '$nombreSurtidor'?")
            .setPositiveButton("Eliminar") { _, _ ->
                if (nSurtidor.eliminar(idSurtidor)) {
                    Toast.makeText(this, "Surtidor eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarSurtidores()  // Recargar la lista
                } else {
                    Toast.makeText(this, "Error al eliminar el surtidor", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Método para mostrar el diálogo de edición
    private fun mostrarDialogoEditar(surtidor: Surtidor) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Surtidor")

        val input = EditText(this)
        input.setText(surtidor.nombre)
        builder.setView(input)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombre = input.text.toString()
            if (nombre.isNotEmpty()) {
                surtidor.nombre = nombre
                if (nSurtidor.editar(surtidor)) {
                    Toast.makeText(this, "Surtidor actualizado", Toast.LENGTH_SHORT).show()
                    cargarSurtidores() // Recargar la lista
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onResume() {
        super.onResume()
        cargarSurtidores()  // Recargar los datos cuando la actividad vuelve a primer plano
    }
}