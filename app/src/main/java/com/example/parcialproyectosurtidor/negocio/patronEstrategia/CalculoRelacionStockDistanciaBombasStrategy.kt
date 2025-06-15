package com.example.parcialproyectosurtidor.negocio.patronEstrategia

import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import kotlin.math.max

/**
 * Estrategia que calcula un "índice de conveniencia" basado en una relación
 * entre el stock disponible, la distancia y el número de bombas.
 * Valores más altos indican mayor conveniencia.
 */
class CalculoRelacionStockDistanciaBombasStrategy : CalculoProbabilidadStrategy {

    // Factores de ponderación (puedes ajustarlos)
    companion object {
        const val FACTOR_STOCK = 0.4
        const val FACTOR_DISTANCIA = -0.3 // Negativo porque más distancia es peor
        const val FACTOR_BOMBAS = 0.3
        const val UMBRAL_CONVENIENTE = 50 // Define un umbral para considerar "conveniente"
        const val UMBRAL_ACEPTABLE = 20
    }

    override fun calcular(
        surtidor: Surtidor,
        stock: StockCombustible?,
        distanciaEnMetros: Double,
        tipoCombustibleNombre: String
    ): String {
        val mensaje = StringBuilder()
        mensaje.append("--- Estrategia: Índice de Conveniencia (Stock/Distancia/Bombas) ---\n")
        mensaje.append("⛽ Surtidor: ${surtidor.nombre} ($tipoCombustibleNombre)\n")
        mensaje.append("Distancia: ${"%.0f".format(distanciaEnMetros)} mts\n")


        if (stock == null || stock.cantidad <= 0) {
            mensaje.append("Stock: No disponible o Agotado\n\n")
            mensaje.append("❌ ÍNDICE BAJO: No hay stock de $tipoCombustibleNombre.")
            return mensaje.toString()
        }

        mensaje.append("Stock: ${"%.0f".format(stock.cantidad)} lts. | Bombas: ${stock.nroBombas}\n")

        // Normalizar valores podría ser útil, pero para simplificar, usamos valores directos con factores
        // Aseguramos que la distancia no sea cero para evitar división por cero si se usara.
        val distanciaNormalizada = max(1.0, distanciaEnMetros / 1000.0) // Distancia en km, mínimo 1 para evitar impacto exagerado de distancias muy cortas

        // El score de stock podría ser logarítmico para que cantidades muy grandes no dominen tanto
        // pero por simplicidad lo dejamos lineal.
        val scoreStock = stock.cantidad / 1000.0 // Score por cada 1000 litros

        val indiceConveniencia = (scoreStock * FACTOR_STOCK) +
                (distanciaNormalizada * FACTOR_DISTANCIA) + // distanciaNormalizada es positiva, factor es negativo
                (stock.nroBombas * FACTOR_BOMBAS)

        mensaje.append("Índice de Conveniencia Calculado: ${"%.2f".format(indiceConveniencia)}\n\n")

        when {
            indiceConveniencia >= UMBRAL_CONVENIENTE -> {
                mensaje.append("✅ PROBABILIDAD ALTA: Índice de conveniencia MUY BUENO (${"%.2f".format(indiceConveniencia)}).")
            }
            indiceConveniencia >= UMBRAL_ACEPTABLE -> {
                mensaje.append("⚠️ PROBABILIDAD MEDIA: Índice de conveniencia ACEPTABLE (${"%.2f".format(indiceConveniencia)}).")
            }
            else -> {
                mensaje.append("❌ PROBABILIDAD BAJA: Índice de conveniencia BAJO (${"%.2f".format(indiceConveniencia)}). Considera otras opciones.")
            }
        }
        return mensaje.toString()
    }
}