package com.example.parcialproyectosurtidor.negocio.patronEstrategia

import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor

/**
 * Interfaz para definir diferentes estrategias de cálculo de probabilidad de abastecimiento.
 * Cada estrategia implementará su propia lógica para determinar la conveniencia.
 */
interface CalculoProbabilidadStrategy {
    /**
     * Calcula la probabilidad/conveniencia de abastecimiento.
     *
     * @param surtidor El surtidor seleccionado.
     * @param stock El stock de combustible relevante en el surtidor. Puede ser null si no hay stock del tipo deseado.
     * @param distanciaEnMetros La distancia calculada desde el surtidor hasta el punto de destino.
     * @param tipoCombustibleNombre El nombre del tipo de combustible seleccionado por el usuario.
     * @return Un String con el mensaje del resultado del cálculo.
     */
    fun calcular(
        surtidor: Surtidor,
        stock: StockCombustible?,
        distanciaEnMetros: Double,
        tipoCombustibleNombre: String
    ): String
}