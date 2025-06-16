// ========================
// ARCHIVO MODIFICADO: GestionarTiposCombustibleActivity.kt
// Aplicación del Patrón Observer para notificar actualizaciones al cambiar la lista de tipos de combustible
// ========================

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
import com.example.parcialproyectosurtidor.presentacion.observer.ResultadoObserver
import com.example.parcialproyectosurtidor.presentacion.observer.ResultadoPublisher

class GestionarTiposCombustibleActivity : AppCompatActivity(), ResultadoObserver {

    private lateinit var nTipoCombustible: NTipoCombustible
    private lateinit var linearLayout: LinearLayout
    private lateinit var publisher: ResultadoPublisher // ⬅️ Publisher Observer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_tipos_combustible)

        nTipoCombustible = NTipoCombustible(this)
        linearLayout = findViewById(R.id.linear_layout_tipos_combustible)

        publisher = ResultadoPublisher() // Creamos el publisher
        publisher.subscribe(this) // Suscribimos esta actividad como observadora

        cargarTiposCombustible() // Cargar inicialmente

        findViewById<Button>(R.id.btn_agregar_tipo_combustible).setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    private fun cargarTiposCombustible() {
        val tiposCombustible = nTipoCombustible.obtenerTodos()
        linearLayout.removeAllViews()

        for (tipoCombustible in tiposCombustible) {
            val nombreTextView = TextView(this).apply {
                text = tipoCombustible.nombre
                textSize = 18f
            }

            val editarButton = Button(this).apply {
                text = "Editar"
                setOnClickListener {
                    mostrarDialogoEditar(tipoCombustible)
                }
            }

            val eliminarButton = Button(this).apply {
                text = "Eliminar"
                setOnClickListener {
                    mostrarDialogoEliminar(tipoCombustible)
                }
            }

            linearLayout.addView(nombreTextView)
            linearLayout.addView(editarButton)
            linearLayout.addView(eliminarButton)
        }
    }

    private fun mostrarDialogoAgregar() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Tipo de Combustible")

        val input = EditText(this)
        input.hint = "Nombre del Combustible"
        builder.setView(input)

        builder.setPositiveButton("Agregar") { dialog, _ ->
            val nombre = input.text.toString()
            if (nombre.isNotEmpty()) {
                nTipoCombustible.crear(nombre)
                publisher.notifyObservers() // ⬅️ Notificamos a todos los observers
                dialog.dismiss()
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarDialogoEditar(tipoCombustible: TipoCombustible) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Tipo de Combustible")

        val input = EditText(this)
        input.setText(tipoCombustible.nombre)
        builder.setView(input)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombre = input.text.toString()
            if (nombre.isNotEmpty()) {
                tipoCombustible.nombre = nombre
                nTipoCombustible.editar(tipoCombustible)
                publisher.notifyObservers() // ⬅️ Notificamos cambios
                dialog.dismiss()
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarDialogoEliminar(tipoCombustible: TipoCombustible) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Tipo de Combustible")
            .setMessage("¿Estás seguro de que deseas eliminar este tipo de combustible?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                val eliminado = nTipoCombustible.eliminar(tipoCombustible.id)
                if (eliminado) {
                    Toast.makeText(this, "Eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    publisher.notifyObservers() // ⬅️ Notificamos a todos los observers
                } else {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    override fun update(mensaje: String) {
        // Por ahora simplemente recargamos al ser notificados
        cargarTiposCombustible()
    }

    override fun onDestroy() {
        super.onDestroy()
        publisher.unsubscribe(this) // Cancelamos suscripción al destruirse la Activity
    }
}
