package com.example.clubdeportivog3.model

data class Socio(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val correo: String?,
    val telefono: String,
    val cuota: Double,
    val aptoFisico: Boolean,
    val carnetEntregado: Boolean,
    val pagoAlDia: Boolean
)