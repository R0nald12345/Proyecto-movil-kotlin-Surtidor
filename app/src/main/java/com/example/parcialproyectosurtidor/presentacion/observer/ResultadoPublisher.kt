package com.example.parcialproyectosurtidor.presentacion.observer

import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import com.example.parcialproyectosurtidor.negocio.patronEstrategia.CalculoProbabilidadStrategy

class ResultadoPublisher {
    private val observers = mutableListOf<ResultadoObserver>()
    private var mensajeResultado: String = ""

    fun subscribe(observer: ResultadoObserver) {
        observers.add(observer)
    }

    fun unsubscribe(observer: ResultadoObserver) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        observers.forEach { it.update(mensajeResultado) }
    }

    fun calcularResultado(
        estrategia: CalculoProbabilidadStrategy,
        surtidor: Surtidor,
        stock: StockCombustible?,
        distancia: Double,
        tipoCombustible: String
    ) {
        mensajeResultado = estrategia.calcular(surtidor, stock, distancia, tipoCombustible)
        notifyObservers()
    }
}