package com.example.parcialproyectosurtidor.negocio

import android.content.Context
import com.example.parcialproyectosurtidor.datos.dao.DTipoCombustible
import com.example.parcialproyectosurtidor.datos.entidades.TipoCombustible

class NTipoCombustible(context: Context) {

    private val dao = DTipoCombustible(context)

    fun crear(nombre: String): Boolean {
        val nuevo = TipoCombustible(id = 0, nombre = nombre.trim())
        return dao.crear(nuevo)
    }

    fun obtenerTodos(): List<TipoCombustible> {
        return dao.getTipoCombustible()
    }

    fun obtenerPorId(id: Int): TipoCombustible? {
        return dao.getTipoCombustibleId(id)
    }

    // Método para editar un tipo de combustible
    fun editar(tipoCombustible: TipoCombustible): Boolean {
        return dao.editar(tipoCombustible)
    }

    // Método para eliminar un tipo de combustible
    fun eliminar(id: Int): Boolean {
        return dao.eliminar(id)
    }
}