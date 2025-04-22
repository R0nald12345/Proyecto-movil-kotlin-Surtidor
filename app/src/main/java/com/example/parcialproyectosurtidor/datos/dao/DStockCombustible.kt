package com.example.parcialproyectosurtidor.datos.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.parcialproyectosurtidor.datos.conexion.Conexion
import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible

class DStockCombustible (private val context: Context) {
    private val conexion = Conexion(context)
    private var db: SQLiteDatabase? = null

    fun crear(stock: StockCombustible): Boolean {
        return try {
            db = conexion.writableDatabase
            val values = ContentValues().apply {
                put("id_surtidor", stock.idSurtidor)
                put("id_tipo_combustible", stock.idTipoCombustible)
                put("cantidad", stock.cantidad)
                put("nroBombas", stock.nroBombas)
            }
            val resultado = db?.insert("StockCombustible", null, values)
            resultado != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db?.close()
        }
    }

    fun getStockPorIdSurtidor(idSurtidor: Int): List<StockCombustible> {
        val lista = mutableListOf<StockCombustible>()
        try {
            db = conexion.readableDatabase
            val cursor = db!!.rawQuery("SELECT * FROM StockCombustible WHERE id_surtidor = ?", arrayOf(idSurtidor.toString()))
            if (cursor.moveToFirst()) {
                do {
                    val stock = StockCombustible(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        idSurtidor = cursor.getInt(cursor.getColumnIndexOrThrow("id_surtidor")),
                        idTipoCombustible = cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_combustible")),
                        cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad")),
                        nroBombas = cursor.getInt(cursor.getColumnIndexOrThrow("nroBombas"))
                    )
                    lista.add(stock)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return lista
    }

    fun eliminarPorIdSurtidor(idSurtidor: Int): Boolean {
        return try {
            db = conexion.writableDatabase
            val filas = db?.delete("StockCombustible", "id_surtidor = ?", arrayOf(idSurtidor.toString()))
            filas != null && filas > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db?.close()
        }
    }
}