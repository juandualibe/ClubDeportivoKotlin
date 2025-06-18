package com.example.clubdeportivog3.model

data class NoSocio (
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val correo: String?,
    val telefono: String,
    val pagoDiario: Double,
    val aptoFisico: Boolean,
)