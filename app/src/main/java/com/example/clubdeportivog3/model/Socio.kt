package com.example.clubdeportivog3.model
/**
 * Clase de datos que representa a un socio.
 * Contiene toda la información personal y administrativa de un socio.
 * Esta clase se utiliza tanto para la gestión en la interfaz como para
 * el almacenamiento en la base de datos.
 */
data class Socio(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val correo: String?,
    val telefono: String,
    val cuota: Double,
    val aptoFisico: Boolean,
    var carnetEntregado: Boolean,
    val pagoAlDia: Boolean
)