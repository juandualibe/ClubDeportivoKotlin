package com.example.clubdeportivog3.model

data class Actividad (
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val dia: String,
    val horario: String,
    val monto: Double,
    val cupoMaximo: Int
)