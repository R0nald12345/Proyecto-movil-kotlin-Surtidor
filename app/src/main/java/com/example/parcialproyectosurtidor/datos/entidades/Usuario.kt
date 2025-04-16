package com.example.parcialproyectosurtidor.datos.entidades

data class Usuario(
    var id: Int = 0,
    var nombre: String = "",
    var telefono: String = "",
    var latitud: Double = 0.0,
    var longitud: Double = 0.0
)
