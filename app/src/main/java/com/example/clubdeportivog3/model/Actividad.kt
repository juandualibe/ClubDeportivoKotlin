package com.example.clubdeportivog3.model

/**
 * Clase de datos que representa a una actividad.
 * Contiene toda la información personal y administrativa de una actividad.
 * Esta clase se utiliza tanto para la gestión en la interfaz como para
 * el almacenamiento en la base de datos.
 */

data class Actividad (
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val dia: String,
    val horario: String,
    val monto: Double,
    val cupoMaximo: Int
)