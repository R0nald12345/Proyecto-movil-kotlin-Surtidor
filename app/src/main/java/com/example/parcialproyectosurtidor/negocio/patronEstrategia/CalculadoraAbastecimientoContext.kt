package com.example.parcialproyectosurtidor.negocio.patronEstrategia

import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor

class CalculadoraAbastecimientoContext {

    private var strategy: CalculoProbabilidadStrategy = CalculoSimpleDistanciaStrategy()

    fun setStrategy(strategy: CalculoProbabilidadStrategy) {
        this.strategy = strategy
    }

    fun calcular(
        surtidor: Surtidor,
        stock: StockCombustible?,
        distancia: Double,
        tipoCombustible: String
    ): String {
        return strategy.calcular(surtidor, stock, distancia, tipoCombustible)
    }
}