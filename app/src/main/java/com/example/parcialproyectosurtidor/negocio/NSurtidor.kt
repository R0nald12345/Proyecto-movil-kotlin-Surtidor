package com.example.parcialproyectosurtidor.negocio

import android.content.Context
import com.example.parcialproyectosurtidor.datos.dao.DSurtidor
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class NSurtidor(private val context: Context) {

    private val dSurtidor = DSurtidor(context)

    fun crear(surtidor: Surtidor): Boolean {
        // Aquí surtidor ya contiene cantidadBombas
        return dSurtidor.crear(surtidor)
    }

    fun editar(surtidor: Surtidor): Boolean {
        return dSurtidor.editar(surtidor)
    }

    fun eliminar(id: Int): Boolean {
        return dSurtidor.eliminar(id)
    }

    fun getSurtidores(): List<Surtidor> {
        return dSurtidor.getSurtidores()
    }

    fun getSurtidorPorId(id: Int): Surtidor? {
        return dSurtidor.getSurtidorPorId(id)
    }

    fun getSurtidorMasCercano(latitudUsuario: Double, longitudUsuario: Double): Surtidor? {
        val surtidores = dSurtidor.getSurtidores()
        if (surtidores.isEmpty()) return null

        var surtidorMasCercano: Surtidor? = null
        var distanciaMinima = Double.MAX_VALUE

        for (surtidor in surtidores) {
            val distancia = calcularDistancia(
                latitudUsuario, longitudUsuario,
                surtidor.latitud, surtidor.longitud
            )

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia
                surtidorMasCercano = surtidor
            }
        }

        return surtidorMasCercano
    }

    private fun calcularDistancia(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val radioTierra = 6371.0 // km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radioTierra * c
    }
}
