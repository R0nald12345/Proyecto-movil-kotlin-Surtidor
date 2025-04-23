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
        return dSurtidor.crear(surtidor)
    }

    fun editar(surtidor: Surtidor): Boolean {
        return dSurtidor.editar(surtidor)
    }

    fun eliminar(id: Int): Boolean {
        return dSurtidor.eliminar(id)
    }

    fun obtenerTodos(): List<Surtidor> {
        return dSurtidor.getSurtidores()
    }

    fun obtenerPorId(id: Int): Surtidor? {
        return dSurtidor.getSurtidorPorId(id)
    }

    /**
     * Retorna el surtidor más cercano a la ubicación del usuario
     */
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

    /**
     * Calcula la distancia entre dos coordenadas geográficas (Haversine)
     */
    private fun calcularDistancia(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val radioTierra = 6371.0 // Radio de la Tierra en km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radioTierra * c
    }


    private fun Double.pow(exp: Double): Double = Math.pow(this, exp)
}