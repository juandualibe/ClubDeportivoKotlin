package com.example.clubdeportivog3.data

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.activities.SocioDetailsActivity
import com.example.clubdeportivog3.model.Socio

/**
 * Adaptador para mostrar socios en una lista.
 */
class AdaptadorSocios(
    private var listaSocios: List<Socio>, // Lista de socios
    private val onAccionSocio: (accion: String, socio: Socio) -> Unit // Callback para acciones
) : RecyclerView.Adapter<AdaptadorSocios.ViewHolder>() {

    // Clase para cada item de la lista
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconoSocio: ImageView = itemView.findViewById(R.id.iconoSocio)
        val textoSocio: TextView = itemView.findViewById(R.id.textoSocio)
        val iconoEditar: ImageView = itemView.findViewById(R.id.iconoEditar)
        val iconoEliminar: ImageView = itemView.findViewById(R.id.iconoEliminar)
    }

    // Crea la vista de cada item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return ViewHolder(view)
    }

    // Actualiza la lista de socios
    fun actualizarLista(nuevaLista: List<Socio>) {
        listaSocios = nuevaLista
        notifyDataSetChanged()
    }

    // Llena los datos de cada item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val socio = listaSocios[position]
        holder.textoSocio.text = "${socio.nombre} ${socio.apellido}" // Muestra nombre y apellido

        // Al tocar el texto, va a los detalles del socio
        holder.textoSocio.setOnClickListener {
            val intent = Intent(holder.itemView.context, SocioDetailsActivity::class.java).apply {
                putExtra("SOCIO_ID", socio.id)
                putExtra("SOCIO_NOMBRE", socio.nombre)
                putExtra("SOCIO_APELLIDO", socio.apellido)
            }
            holder.itemView.context.startActivity(intent)
        }

        // Botón para editar
        holder.iconoEditar.setOnClickListener {
            onAccionSocio("editar", socio)
        }

        // Botón para eliminar con confirmación
        holder.iconoEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea eliminar a ${socio.nombre} ${socio.apellido}?")
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Sí") { _, _ -> onAccionSocio("eliminar", socio) }
                .setCancelable(true)
                .show()
        }
    }

    // Devuelve la cantidad de items
    override fun getItemCount(): Int = listaSocios.size
}