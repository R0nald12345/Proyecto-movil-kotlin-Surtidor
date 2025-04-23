package com.example.parcialproyectosurtidor.presentacion.adapter

import android.view.LayoutInflater
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialproyectosurtidor.R

class SurtidorAdapter(
    private val surtidores: List<Surtidor>,
    private val editarListener: (Surtidor) -> Unit,
    private val eliminarListener: (Surtidor) -> Unit
) : RecyclerView.Adapter<SurtidorAdapter.SurtidorViewHolder>() {

    class SurtidorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreSurtidor: TextView = itemView.findViewById(R.id.tvNombreSurtidor)
        //val tvCoordenadas: TextView = itemView.findViewById(R.id.tvCoordenadas)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurtidorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_surtidor, parent, false)
        return SurtidorViewHolder(view)
    }

    // Método para actualizar la lista de surtidores
    override fun onBindViewHolder(holder: SurtidorViewHolder, position: Int) {
        val surtidor = surtidores[position]

        holder.tvNombreSurtidor.text = surtidor.nombre
       // holder.tvCoordenadas.text = "Lat: ${surtidor.latitud}, Long: ${surtidor.longitud}"

        holder.btnEditar.setOnClickListener {
            editarListener(surtidor)
        }

        holder.btnEliminar.setOnClickListener {
            eliminarListener(surtidor)
        }
    }

    override fun getItemCount(): Int = surtidores.size
}