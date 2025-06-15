package com.example.parcialproyectosurtidor.negocio.patronEstrategia

import com.example.parcialproyectosurtidor.datos.entidades.StockCombustible
import com.example.parcialproyectosurtidor.datos.entidades.Surtidor
import kotlin.math.ceil

/**
 * Estrategia que calcula la probabilidad basada en el tiempo de espera estimado,
 * considerando la cantidad de autos por delante, el stock disponible y el número de bombas.
 */
class CalculoTiempoEsperaStrategy : CalculoProbabilidadStrategy {

    override fun calcular(
        surtidor: Surtidor,
        stock: StockCombustible?,
        distanciaEnMetros: Double,
        tipoCombustibleNombre: String
    ): String {
        // --- Inicio de la lógica de cálculo (extraída de tu Activity) ---
        if (stock == null) {
            return "Este surtidor no tiene $tipoCombustibleNombre."
        }

        // Estimación simple de autos basada en distancia (puedes refinar esto)
        // Por ejemplo, si cada "cuadra" o segmento es X metros y estimas Y autos por segmento.
        // Aquí mantendremos tu lógica original de distancia / 5.0
        val cantidadAutosDelanteEstimada = (distanciaEnMetros / 5.0).toInt() // Considera si esta métrica es la adecuada.
        // Originalmente, esta distancia era la ruta del usuario.
        // Si la distancia es al surtidor, ¿cómo estimamos los autos?
        // Por ahora, asumiremos que esta distancia es un proxy de la "demanda" o "lejanía".

        val litrosDisponibles = stock.cantidad
        val bombas = stock.nroBombas
        val litrosPorAuto = 45.0 // Litros promedio que carga un auto
        val tiempoPorAutoEnMinutos = 6.0 // Tiempo promedio que tarda un auto en cargar
        val autosAtendidosSimultaneamentePorBomba = 2 // Asumimos que cada bomba puede atender a 2 autos (uno a cada lado) o ajustar según realidad
        val capacidadTotalAtencionSimultanea = bombas * autosAtendidosSimultaneamentePorBomba

        val litrosNecesariosParaAutosDelante = cantidadAutosDelanteEstimada * litrosPorAuto
        val alcanzaCombustible = litrosDisponibles >= litrosNecesariosParaAutosDelante

        // Si no hay capacidad de atención o no hay autos, el tiempo de espera es 0 o no aplica.
        if (capacidadTotalAtencionSimultanea <= 0 || cantidadAutosDelanteEstimada <= 0) {
            return if (alcanzaCombustible) {
                "⛽ El surtidor '${surtidor.nombre}' tiene $tipoCombustibleNombre.\n" +
                        "Distancia: ${"%.0f".format(distanciaEnMetros)} mts.\n" +
                        "Stock: ${"%.0f".format(litrosDisponibles)} lts. | Bombas: $bombas\n\n" +
                        "✅ Parece haber poca o ninguna espera y el combustible alcanzaría."
            } else {
                "⛽ El surtidor '${surtidor.nombre}' tiene $tipoCombustibleNombre.\n" +
                        "Distancia: ${"%.0f".format(distanciaEnMetros)} mts.\n" +
                        "Stock: ${"%.0f".format(litrosDisponibles)} lts. | Bombas: $bombas\n\n" +
                        "⚠️ Aunque no habría espera, el stock podría no ser suficiente para la demanda estimada."
            }
        }

        // TANDAS: Cuántos grupos de autos tienen que pasar antes que yo (o los 'cantidadAutosDelanteEstimada')
        // Si hay 10 autos y puedo atender 4 a la vez, son ceil(10/4) = 3 tandas.
        val tandasNecesarias = ceil(cantidadAutosDelanteEstimada.toDouble() / capacidadTotalAtencionSimultanea.toDouble())
        val tiempoEsperaTotalEnMinutos = (tandasNecesarias * tiempoPorAutoEnMinutos).toInt()
        // --- Fin de la lógica de cálculo ---

        val mensaje = StringBuilder()
        mensaje.append("--- Estrategia: Tiempo de Espera Estimado ---\n")
        mensaje.append("⛽ Surtidor: ${surtidor.nombre} ($tipoCombustibleNombre)\n")
        mensaje.append("Distancia: ${"%.0f".format(distanciaEnMetros)} mts\n")
        mensaje.append("Stock: ${"%.0f".format(litrosDisponibles)} lts | Bombas: $bombas\n")
        mensaje.append("Autos estimados adelante: $cantidadAutosDelanteEstimada\n")
        mensaje.append("Tiempo de Espera Estimado: ${tiempoEsperaTotalEnMinutos / 60}h ${tiempoEsperaTotalEnMinutos % 60}min\n\n")

        mensaje.append(
            if (alcanzaCombustible) {
                "✅ Las probabilidades son ALTAS: el tiempo de espera es manejable y el combustible alcanzaría."
            } else {
                "❌ Las probabilidades son BAJAS: aunque el tiempo de espera sea X, el combustible probablemente NO alcance para todos."
            }
        )
        return mensaje.toString()
    }
}