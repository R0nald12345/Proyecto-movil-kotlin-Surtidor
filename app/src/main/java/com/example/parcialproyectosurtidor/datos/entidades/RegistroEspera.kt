package com.example.parcialproyectosurtidor.datos.entidades

data class RegistroEspera(
    var id: Int = 0,
    var horaInicio: String = "",
    var cantidadAutoAntes: Int = 0,
    var tiempoEsperaEstimado: Int = 0,
    var cantidadCombustible: Double = 0.0,
    var idUsuario: Int = 0,
    var idSurtidor: Int = 0
)
