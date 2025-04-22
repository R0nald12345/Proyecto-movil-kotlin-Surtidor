package com.example.parcialproyectosurtidor.presentacion.TipoCombustible

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialproyectosurtidor.R
import com.example.parcialproyectosurtidor.datos.entidades.TipoCombustible
import com.example.parcialproyectosurtidor.negocio.NTipoCombustible

class GestionarTiposCombustibleActivity : AppCompatActivity() {

    private lateinit var nTipoCombustible: NTipoCombustible
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_tipos_combustible)

        nTipoCombustible = NTipoCombustible(this)

        // Configuración del contenedor de tipos de combustible
        linearLayout = findViewById(R.id.linear_layout_tipos_combustible)

        // Cargar los tipos de combustible en el contenedor
        cargarTiposCombustible()

        // Botón "Agregar"
        findViewById<Button>(R.id.btn_agregar_tipo_combustible).setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    // Cargar los tipos de combustible
    private fun cargarTiposCombustible() {
        val tiposCombustible = nTipoCombustible.obtenerTodos()

        // Limpiar el LinearLayout antes de cargar nuevos elementos
        linearLayout.removeAllViews()

        for (tipoCombustible in tiposCombustible) {
            // Crear un TextView para el nombre del combustible
            val nombreTextView = TextView(this).apply {
                text = tipoCombustible.nombre
                textSize = 18f
            }

            // Crear el botón de editar
            val editarButton = Button(this).apply {
                text = "Editar"
                setOnClickListener {
                    mostrarDialogoEditar(tipoCombustible)
                }
            }

            // Crear el botón de eliminar
            val eliminarButton = Button(this).apply {
                text = "Eliminar"
                setOnClickListener {
                    mostrarDialogoEliminar(tipoCombustible)
                }
            }

            // Agregar las vistas al contenedor
            linearLayout.addView(nombreTextView)
            linearLayout.addView(editarButton)
            linearLayout.addView(eliminarButton)
        }
    }


    // Método para mostrar el modal de agregar
    private fun mostrarDialogoAgregar() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Tipo de Combustible")

        val input = EditText(this)
        input.hint = "Nombre del Combustible"
        builder.setView(input)

        builder.setPositiveButton("Agregar") { dialog, _ ->
            val nombre = input.text.toString()
            if (nombre.isNotEmpty()) {
                // Lógica para agregar el tipo de combustible
                nTipoCombustible.crear(nombre)
                cargarTiposCombustible() // Recargar la lista
                dialog.dismiss()
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    // Método para mostrar el modal de editar
    private fun mostrarDialogoEditar(tipoCombustible: TipoCombustible) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Tipo de Combustible")

        val input = EditText(this)
        input.setText(tipoCombustible.nombre)
        builder.setView(input)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombre = input.text.toString()
            if (nombre.isNotEmpty()) {
                // Lógica para editar el tipo de combustible
                tipoCombustible.nombre = nombre
                nTipoCombustible.editar(tipoCombustible)
                cargarTiposCombustible() // Recargar la lista
                dialog.dismiss()
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    // Método para mostrar el modal de eliminar
    private fun mostrarDialogoEliminar(tipoCombustible: TipoCombustible) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Tipo de Combustible")
            .setMessage("¿Estás seguro de que deseas eliminar este tipo de combustible?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                val eliminado = nTipoCombustible.eliminar(tipoCombustible.id)
                if (eliminado) {
                    Toast.makeText(this, "Eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    cargarTiposCombustible() // Recargar la lista
                } else {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }
}
