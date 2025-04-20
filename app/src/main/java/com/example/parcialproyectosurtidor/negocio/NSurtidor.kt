package com.example.parcialproyectosurtidor.negocio

import android.content.Context
import com.example.parcialproyectosurtidor.datos.dao.DSurtidor
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//val es inmutable no cambia su valor
class NSurtidor(private val context: Context) {

    // Instancia de la capa de acceso a datos (DAO)
    private val dSurtidor = DSurtidor(context)


    //Logica para crear nuevo Surtidor
    fun crear(surtidor: Surtidor):Boolean{
        return dSurtidor.crear(surtidor)
    }

    //Logica para editar un surtidor existente
    fun editar(surtidor: Surtidor):Boolean{
        return dSurtidor.editar(surtidor)
    }

    //Logica para eliminar un Surtidor por ID
    fun eliminar(id:Int):Boolean{
        return dSurtidor.eliminar(id)
    }

    //Obtener todos los Surtidores de la Base de Datos
    fun getSurtidores(): List<Surtidor>{
        return dSurtidor.getSurtidores()
    }

    fun getSurtidorPorId(id:Int): Surtidor?{
        return dSurtidor.getSurtidorPorId(id)
    }

    //Me Retorna el surtidor mas sercado de acuerdo a las coordenadas del usuario
    // Método de negocio específico: encontrar surtidor más cercano
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

    // Función auxiliar: fórmula haversine para calcular distancia entre coordenadas
    private fun calcularDistancia(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val radioTierra = 6371.0 // Radio de la Tierra en km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radioTierra * c // Distancia en km
    }


}