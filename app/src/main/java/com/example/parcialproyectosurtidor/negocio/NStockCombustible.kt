package com.example.parcialproyectosurtidor.negocio

import android.content.Context
import com.example.parcialproyectosurtidor.datos.dao.DStockCombustible
import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible

class NStockCombustible(context: Context) {

    private val dao = DStockCombustible(context)

    fun crear(stock: StockCombustible): Boolean {
        return dao.crear(stock)
    }

    fun obtenerPorSurtidor(idSurtidor: Int): List<StockCombustible> {
        return dao.getStockPorIdSurtidor(idSurtidor)
    }

    fun eliminarPorSurtidor(idSurtidor: Int): Boolean {
        return dao.eliminarPorIdSurtidor(idSurtidor)
    }
}