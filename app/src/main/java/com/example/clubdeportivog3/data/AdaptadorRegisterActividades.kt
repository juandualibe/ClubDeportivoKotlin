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

/**
 * Adaptador para inscribir socios en actividades.
 */
class AdaptadorRegisterActividades(
    private val actividades: MutableList<Actividad>, // Lista de actividades
    private val socioNumero: Int, // ID del socio
    private val socioNombre: String // Nombre del socio
) : RecyclerView.Adapter<AdaptadorRegisterActividades.ViewHolder>() {

    // Clase que representa cada item de la lista
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre: TextView = itemView.findViewById(R.id.textoNombre)
        val textoDescripcion: TextView = itemView.findViewById(R.id.textoDescripcion)
        val btnInscribir: Button = itemView.findViewById(R.id.btnInscribir)
    }

    // Crea la vista para cada item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_register_actividad, parent, false)
        return ViewHolder(view)
    }

    // Llena los datos de cada item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = actividades[position]
        val db = ClubDeportivoBD(holder.itemView.context)

        // Busca el socio
        val socio = db.obtenerSocio(socioNumero)
        // Calcula el cupo disponible
        val cupoDisponible = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)

        holder.textoNombre.text = actividad.nombre // Muestra el nombre
        holder.textoDescripcion.text = "${actividad.descripcion}\nCupo disponible: $cupoDisponible" // Muestra descripción y cupo

        // Botón para inscribir al socio
        holder.btnInscribir.setOnClickListener {
            val cupoActualizado = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)
            if (socio == null) {
                // Error si no encuentra el socio
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Error")
                    .setMessage("No se pudo encontrar la información del socio.")
                    .setPositiveButton("Aceptar", null)
                    .show()
                return@setOnClickListener
            }

            // Confirma la inscripción
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea inscribir al socio a esta actividad?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí") { _, _ ->
                    if (db.estaInscripto(socioNumero, actividad.id)) {
                        // Ya está inscripto
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Ya inscripto")
                            .setMessage("El socio ya está inscripto en esta actividad.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else if (cupoActualizado <= 0) {
                        // Sin cupo disponible
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Sin cupo")
                            .setMessage("No hay cupos disponibles para esta actividad.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else if (!socio.aptoFisico) {
                        // Falta apto físico
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Apto físico requerido")
                            .setMessage("El socio debe presentar el apto físico para inscribirse en actividades.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else {
                        // Inscribe al socio
                        val exito = db.inscribirSocioEnActividad(socioNumero, actividad.id)
                        if (exito) {
                            notifyItemChanged(position) // Actualiza la vista
                            val intent = Intent(holder.itemView.context, AddedRegistrationAceptedActivity::class.java).apply {
                                putExtra("SOCIO_NUMERO", socioNumero)
                                putExtra("SOCIO_NOMBRE", socioNombre)
                                putExtra("ACTIVIDAD_NOMBRE", actividad.nombre)
                            }
                            holder.itemView.context.startActivity(intent)
                        } else {
                            AlertDialog.Builder(holder.itemView.context)
                                .setTitle("Error")
                                .setMessage("Ocurrió un error al intentar inscribir al socio.")
                                .setPositiveButton("Aceptar", null)
                                .show()
                        }
                    }
                }
                .setCancelable(true)
                .show()
        }

        db.close() // Cierra la base de datos
    }

    // Devuelve la cantidad de items
    override fun getItemCount(): Int = actividades.size
}