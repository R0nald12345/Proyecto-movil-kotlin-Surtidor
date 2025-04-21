package com.example.parcialproyectosurtidor.datos.entidades

data class Surtidor(
    val id: Int,
    var nombre: String,
    var latitud: Double,
    var longitud: Double,
    var cantidadBombas: Int
)