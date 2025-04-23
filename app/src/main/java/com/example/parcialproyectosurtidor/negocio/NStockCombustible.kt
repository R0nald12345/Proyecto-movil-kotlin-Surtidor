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

    fun eliminarPorIdSurtidor(idSurtidor: Int): Boolean {
        return dao.eliminarPorIdSurtidor(idSurtidor)
    }

    // Eliminar el stock asociado al surtidor y agregar nuevos registros
    fun actualizarStock(idSurtidor: Int, stockList: List<StockCombustible>): Boolean {
        // Eliminar stock previo
        if (eliminarPorIdSurtidor(idSurtidor)) {
            // Crear nuevo stock
            for (stock in stockList) {
                if (!crear(stock)) {
                    return false // Si alguna inserción falla, revertir todo
                }
            }
            return true
        }
        return false
    }
}