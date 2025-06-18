package com.example.clubdeportivog3.data

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.activities.AddedRegistrationAceptedActivity
import com.example.clubdeportivog3.model.Actividad
import com.example.clubdeportivog3.model.NoSocio

/**
 * Adaptador para inscribir no socios en actividades.
 */
class AdaptadorRegisterActividadesNoSocio(
    private val actividades: MutableList<Actividad>, // Lista de actividades
    private val noSocioNumero: Int, // ID del no socio
    private val noSocioNombre: String // Nombre del no socio
) : RecyclerView.Adapter<AdaptadorRegisterActividadesNoSocio.ViewHolder>() {

    // Clase para cada item de la lista
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre: TextView = itemView.findViewById(R.id.textoNombre)
        val textoDescripcion: TextView = itemView.findViewById(R.id.textoDescripcion)
        val btnInscribir: Button = itemView.findViewById(R.id.btnInscribir)
    }

    // Crea la vista de cada item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_register_actividad, parent, false)
        return ViewHolder(view)
    }

    // Llena los datos de cada item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = actividades[position]
        val db = ClubDeportivoBD(holder.itemView.context)

        // Busca el no socio
        val noSocio = db.obtenerNoSocio(noSocioNumero)
        // Calcula el cupo disponible
        val cupoDisponible = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)

        // Muestra nombre, descripción, cupo y precio
        holder.textoNombre.text = actividad.nombre
        holder.textoDescripcion.text = "${actividad.descripcion}\nCupo disponible: $cupoDisponible\nPrecio: $${actividad.monto}"

        // Botón para inscribir
        holder.btnInscribir.setOnClickListener {
            val cupoActualizado = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)
            if (noSocio == null) {
                // Error si no encuentra el no socio
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Error")
                    .setMessage("No se pudo encontrar la información del no socio.")
                    .setPositiveButton("Aceptar", null)
                    .show()
                return@setOnClickListener
            }

            // Confirma la inscripción
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea inscribir al no socio a esta actividad?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí") { _, _ ->
                    if (db.estaNoSocioInscriptoEnActividad(noSocioNumero, actividad.id)) {
                        // Ya está inscripto
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Ya inscripto")
                            .setMessage("El no socio ya está inscripto en esta actividad.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else if (cupoActualizado <= 0) {
                        // Sin cupo
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Sin cupo")
                            .setMessage("No hay cupos disponibles para esta actividad.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else if (!noSocio.aptoFisico) {
                        // Falta apto físico
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Apto físico requerido")
                            .setMessage("El no socio debe presentar el apto físico para inscribirse en actividades.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else {
                        // Verifica si el pago diario cubre el monto
                        if (noSocio.pagoDiario < actividad.monto) {
                            // Pide actualizar el pago
                            AlertDialog.Builder(holder.itemView.context)
                                .setTitle("Pago insuficiente")
                                .setMessage("El no socio debe pagar el monto completo: $${actividad.monto}. Pago actual: $${noSocio.pagoDiario}.\n¿Actualizar pago?")
                                .setPositiveButton("Actualizar pago") { _, _ ->
                                    // Actualiza el pago diario
                                    val noSocioActualizado = NoSocio(
                                        id = noSocio.id, nombre = noSocio.nombre, apellido = noSocio.apellido,
                                        dni = noSocio.dni, correo = noSocio.correo, telefono = noSocio.telefono,
                                        pagoDiario = actividad.monto, aptoFisico = noSocio.aptoFisico
                                    )
                                    val exitoActualizacion = db.actualizarNoSocio(noSocioActualizado)
                                    if (exitoActualizacion) {
                                        inscribirNoSocio(db, holder, position, actividad) // Intenta inscribir
                                    } else {
                                        AlertDialog.Builder(holder.itemView.context)
                                            .setTitle("Error")
                                            .setMessage("No se pudo actualizar el pago del no socio.")
                                            .setPositiveButton("Aceptar", null)
                                            .show()
                                    }
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        } else {
                            // Inscribe directamente
                            inscribirNoSocio(db, holder, position, actividad)
                        }
                    }
                }
                .setCancelable(true)
                .show()
        }

        db.close() // Cierra la base de datos
    }

    // Inscribe al no socio en la actividad
    private fun inscribirNoSocio(db: ClubDeportivoBD, holder: ViewHolder, position: Int, actividad: Actividad) {
        val exito = db.inscribirNoSocioEnActividad(noSocioNumero, actividad.id)
        if (exito) {
            notifyItemChanged(position) // Actualiza la vista
            val intent = Intent(holder.itemView.context, AddedRegistrationAceptedActivity::class.java).apply {
                putExtra("NO_SOCIO_NUMERO", noSocioNumero)
                putExtra("NO_SOCIO_NOMBRE", noSocioNombre)
                putExtra("ACTIVIDAD_NOMBRE", actividad.nombre)
            }
            holder.itemView.context.startActivity(intent)
        } else {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Error")
                .setMessage("Ocurrió un error al intentar inscribir al no socio.")
                .setPositiveButton("Aceptar", null)
                .show()
        }
    }

    // Devuelve la cantidad de items
    override fun getItemCount(): Int = actividades.size
}