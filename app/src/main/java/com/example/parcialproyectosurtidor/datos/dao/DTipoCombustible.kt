package com.example.parcialproyectosurtidor.datos.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.parcialproyectosurtidor.datos.conexion.Conexion
import com.example.parcialproyectosurtidor.datos.entidades.TipoCombustible

class DTipoCombustible(private val context: Context) {
    private val conexion = Conexion(context)
    private var db: SQLiteDatabase? = null

    fun crear(combustible: TipoCombustible): Boolean {
        return try {
            db = conexion.writableDatabase
            val values = ContentValues().apply {
                put("nombre", combustible.nombre)
            }
            val resultado = db?.insert("TipoCombustible", null, values)
            resultado != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db?.close()
        }
    }

    fun getTipoCombustible(): List<TipoCombustible> {
        val lista = mutableListOf<TipoCombustible>()
        try {
            db = conexion.readableDatabase
            val cursor = db!!.rawQuery("SELECT * FROM TipoCombustible", null)
            if (cursor.moveToFirst()) {
                do {
                    val combustible = TipoCombustible(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    )
                    lista.add(combustible)
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

    fun getTipoCombustibleId(id: Int): TipoCombustible? {
        var combustible: TipoCombustible? = null
        try {
            db = conexion.readableDatabase
            val cursor = db!!.rawQuery("SELECT * FROM TipoCombustible WHERE id = ?", arrayOf(id.toString()))
            if (cursor.moveToFirst()) {
                combustible = TipoCombustible(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return combustible
    }


    fun editar(tipoCombustible: TipoCombustible): Boolean {
        try {
            db = conexion.writableDatabase
            val values = ContentValues().apply {
                put("nombre", tipoCombustible.nombre)
            }
            val filasActualizadas = db?.update(
                "TipoCombustible",
                values,
                "id = ?",
                arrayOf(tipoCombustible.id.toString())
            )
            return filasActualizadas != null && filasActualizadas > 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db?.close()
        }
    }

    // Método para eliminar un tipo de combustible
    fun eliminar(id: Int): Boolean {
        try {
            db = conexion.writableDatabase
            val filasEliminadas = db?.delete(
                "TipoCombustible",
                "id = ?",
                arrayOf(id.toString())
            )
            return filasEliminadas != null && filasEliminadas > 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db?.close()
        }
    }
}