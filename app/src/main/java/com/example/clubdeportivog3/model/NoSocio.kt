package com.example.clubdeportivog3.model

/**
 * Clase de datos que representa a un no socio.
 * Contiene toda la información personal y administrativa de un no socio.
 * Esta clase se utiliza tanto para la gestión en la interfaz como para
 * el almacenamiento en la base de datos.
 */

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