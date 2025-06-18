package com.example.clubdeportivog3.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.model.Actividad
import com.example.clubdeportivog3.data.ActividadDetailsActivity

/**
 * Adaptador para mostrar actividades en una lista.
 */
class AdaptadorActividades(
    private val listaActividades: List<Actividad>, // Lista de actividades
    private val onAccionActividad: (accion: String, actividad: Actividad) -> Unit // Callback para acciones
) : RecyclerView.Adapter<AdaptadorActividades.ViewHolder>() {

    // Clase que representa cada item de la lista
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre: TextView = itemView.findViewById(R.id.textoNombre)
        val textoDescripcion: TextView = itemView.findViewById(R.id.textoDescripcion)
        val iconoEditar: ImageView = itemView.findViewById(R.id.iconoEditar)
        val iconoEliminar: ImageView = itemView.findViewById(R.id.iconoEliminar)
        val contenedorTexto: LinearLayout = itemView.findViewById(R.id.contenedorTexto)
    }

    // Crea la vista para cada item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ViewHolder(view)
    }

    // Llena los datos de cada item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = listaActividades[position]
        val db = ClubDeportivoBD(holder.itemView.context)

        // Calcula el cupo disponible
        val cupoDisponible = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)

        holder.apply {
            textoNombre.text = actividad.nombre // Muestra el nombre
            textoDescripcion.text = "${actividad.descripcion}\nCupo máximo: ${actividad.cupoMaximo}\nCupo disponible: $cupoDisponible" // Muestra descripción y cupos

            // Al tocar el texto, va a los detalles
            contenedorTexto.setOnClickListener {
                val intent = Intent(itemView.context, ActividadDetailsActivity::class.java).apply {
                    putExtra("ACTIVIDAD_ID", actividad.id)
                    putExtra("ACTIVIDAD_NOMBRE", actividad.nombre)
                    putExtra("ACTIVIDAD_DESCRIPCION", actividad.descripcion)
                    putExtra("ACTIVIDAD_NUMERO", actividad.id)
                }
                itemView.context.startActivity(intent)
            }
            // Botón para editar
            iconoEditar.setOnClickListener {
                onAccionActividad("editar", actividad)
            }
            // Botón para eliminar
            iconoEliminar.setOnClickListener {
                onAccionActividad("eliminar", actividad)
            }
        }

        db.close() // Cierra la base de datos
    }

    // Devuelve la cantidad de items
    override fun getItemCount(): Int = listaActividades.size
}