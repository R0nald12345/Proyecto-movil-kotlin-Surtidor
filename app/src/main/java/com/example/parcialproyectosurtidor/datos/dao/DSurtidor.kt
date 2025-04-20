package com.example.parcialproyectosurtidor.datos.dao

import android.database.sqlite.SQLiteDatabase
import com.example.parcialproyectosurtidor.datos.conexion.Conexion
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor

class DSurtidor(private val context: Context) {

    //Me creo una instancia de la clase Conexion que trabaja  mi BD
    private val conexion = Conexion(context)

    private var db: SQLiteDatabase? = null

    // Métodos CRUD
    fun crear(surtidor: Surtidor): Boolean {
        try {
            db = conexion.writableDatabase
            val values = ContentValues().apply {
                put("nombre", surtidor.nombre)
                put("latitud", surtidor.latitud)
                put("longitud", surtidor.longitud)

            }

            val id = db?.insert("surtidor", null, values)
            db?.close()

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
                        longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
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
                    longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
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


    // Método para obtener surtidores por usuario
    fun getSurtidoresPorUsuario(idUsuario: Int): List<Surtidor> {
        val lista = mutableListOf<Surtidor>()
        try {
            db = conexion.readableDatabase
            val cursor: Cursor = db!!.rawQuery(
                "SELECT * FROM Surtidor WHERE id_usuario = ?",
                arrayOf(idUsuario.toString())
            )

            if (cursor.moveToFirst()) {
                do {
                    val surtidor = Surtidor(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
                        longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")),
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
}