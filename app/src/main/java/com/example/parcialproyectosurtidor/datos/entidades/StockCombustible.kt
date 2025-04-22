package com.example.parcialproyectosurtidor.datos.entidades

data class StockCombustible(
    val id: Int,
    var idSurtidor: Int,
    var idTipoCombustible: Int,
    var cantidad: Double,
    var nroBombas: Int
)