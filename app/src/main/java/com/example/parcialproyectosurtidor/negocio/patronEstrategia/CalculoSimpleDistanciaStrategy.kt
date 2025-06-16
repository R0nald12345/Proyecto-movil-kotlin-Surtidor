package com.example.parcialproyectosurtidor.negocio.patronEstrategia

import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor

/**
 * Estrategia simple que calcula la probabilidad basada únicamente en la distancia
 * y si hay stock del combustible deseado.
 */
class CalculoSimpleDistanciaStrategy : CalculoProbabilidadStrategy {

    // Definimos umbrales de distancia para categorizar
    companion object {
        const val DISTANCIA_CERCANA_MTS = 1000 // Menos de 1 km es cercano
        const val DISTANCIA_MEDIA_MTS = 3000   // Entre 1 km y 3 km es medio
    }

    override fun calcular(
        surtidor: Surtidor,
        stock: StockCombustible?,
        distanciaEnMetros: Double,
        tipoCombustibleNombre: String
    ): String {
        val mensaje = StringBuilder()
        mensaje.append("--- Estrategia: Distancia y Stock Básico ---\n")
        mensaje.append("⛽ Surtidor: ${surtidor.nombre} ($tipoCombustibleNombre)\n")
        mensaje.append("Distancia: ${"%.0f".format(distanciaEnMetros)} mts\n")

        if (stock == null || stock.cantidad <= 0) {
            mensaje.append("Stock: No disponible o Agotado\n\n")
            mensaje.append("❌ PROBABILIDAD BAJA: No hay stock de $tipoCombustibleNombre en este surtidor.")
            return mensaje.toString()
        }

        mensaje.append("Stock: ${"%.0f".format(stock.cantidad)} lts. | Bombas: ${stock.nroBombas}\n\n")

        when {
            distanciaEnMetros <= DISTANCIA_CERCANA_MTS -> {
                mensaje.append("✅ PROBABILIDAD ALTA: El surtidor está CERCA y tiene stock.")
            }
            distanciaEnMetros <= DISTANCIA_MEDIA_MTS -> {
                mensaje.append("⚠️ PROBABILIDAD MEDIA: El surtidor está a una distancia MODERADA y tiene stock. Considera el tráfico.")
            }
            else -> {
                mensaje.append("❌ PROBABILIDAD BAJA: El surtidor está LEJOS, aunque tenga stock. Puede no ser conveniente.")
            }
        }
        return mensaje.toString()
    }
}