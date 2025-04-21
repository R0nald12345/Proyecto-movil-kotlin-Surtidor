package com.example.parcialproyectosurtidor.datos.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.parcialproyectosurtidor.datos.conexion.Conexion
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor

class DSurtidor(private val context: Context) {

    private val conexion = Conexion(context)
    private var db: SQLiteDatabase? = null

    fun crear(surtidor: Surtidor): Boolean {
        try {
            db = conexion.writableDatabase
            val values = ContentValues().apply {
                put("nombre", surtidor.nombre)
                put("latitud", surtidor.latitud)
                put("longitud", surtidor.longitud)
                put("cantidad_bombas", surtidor.cantidadBombas)
            }

            val id = db?.insert("surtidor", null, values)
            return id != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db?.close()
        }
    }

    fun editar(surtidor: Surtidor): Boolean {
        try {
            db = conexion.writableDatabase
            val values = ContentValues().apply {
                put("nombre", surtidor.nombre)
                put("latitud", surtidor.latitud)
                put("longitud", surtidor.longitud)
                put("cantidad_bombas", surtidor.cantidadBombas)
            }

            val filasActualizadas = db?.update(
                "surtidor",
                values,
                "id = ?",
                arrayOf(surtidor.id.toString())
            )

            return filasActualizadas != null && filasActualizadas > 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db?.close()
        }
    }

    fun eliminar(id: Int): Boolean {
        try {
            db = conexion.writableDatabase
            val filasEliminadas = db?.delete(
                "surtidor",
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

    fun getSurtidores(): List<Surtidor> {
        val lista = mutableListOf<Surtidor>()
        try {
            db = conexion.readableDatabase
            val cursor: Cursor = db!!.rawQuery("SELECT * FROM Surtidor", null)

            if (cursor.moveToFirst()) {
                do {
                    val surtidor = Surtidor(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
                        longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")),
                        cantidadBombas = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_bombas"))
                    )
                    lista.add(surtidor)
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

    fun getSurtidorPorId(id: Int): Surtidor? {
        var surtidor: Surtidor? = null
        try {
            db = conexion.readableDatabase
            val cursor: Cursor = db!!.rawQuery(
                "SELECT * FROM Surtidor WHERE id = ?",
                arrayOf(id.toString())
            )

            if (cursor.moveToFirst()) {
                surtidor = Surtidor(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
                    longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")),
                    cantidadBombas = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_bombas"))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.close()
        }
        return surtidor
    }


}