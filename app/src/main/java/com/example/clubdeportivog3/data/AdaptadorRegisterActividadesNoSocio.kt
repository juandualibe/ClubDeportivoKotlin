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

class AdaptadorRegisterActividadesNoSocio(
    private val actividades: MutableList<Actividad>,
    private val noSocioNumero: Int,
    private val noSocioNombre: String
) : RecyclerView.Adapter<AdaptadorRegisterActividadesNoSocio.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre: TextView = itemView.findViewById(R.id.textoNombre)
        val textoDescripcion: TextView = itemView.findViewById(R.id.textoDescripcion)
        val btnInscribir: Button = itemView.findViewById(R.id.btnInscribir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_register_actividad, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = actividades[position]
        val db = ClubDeportivoBD(holder.itemView.context)

        // Calcular cupo disponible dinámicamente
        val cupoDisponible = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)

        holder.textoNombre.text = actividad.nombre
        holder.textoDescripcion.text = "${actividad.descripcion}\nCupo disponible: $cupoDisponible"

        holder.btnInscribir.setOnClickListener {
            // Recalcular cupo al momento del click
            val cupoActualizado = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)

            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea inscribir al no socio a esta actividad?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí") { _, _ ->

                    if (db.estaNoSocioInscriptoEnActividad(noSocioNumero, actividad.id)) {
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Ya inscripto")
                            .setMessage("El no socio ya está inscripto en esta actividad.")
                            .setPositiveButton("Aceptar", null)
                            .show()

                    } else if (cupoActualizado <= 0) {
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Sin cupo")
                            .setMessage("No hay cupos disponibles para esta actividad.")
                            .setPositiveButton("Aceptar", null)
                            .show()

                    } else {
                        val exito = db.inscribirNoSocioEnActividad(noSocioNumero, actividad.id)
                        if (exito) {
                            // Actualizar la vista inmediatamente
                            notifyItemChanged(position)

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
                }
                .setCancelable(true)
                .show()
        }

        db.close()
    }

    override fun getItemCount(): Int = actividades.size
}