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

class AdaptadorSocios(
    private var listaSocios: List<Socio>,
    private val onAccionSocio: (accion: String, socio: Socio) -> Unit
) : RecyclerView.Adapter<AdaptadorSocios.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconoSocio: ImageView = itemView.findViewById(R.id.iconoSocio)
        val textoSocio: TextView = itemView.findViewById(R.id.textoSocio)
        val iconoEditar: ImageView = itemView.findViewById(R.id.iconoEditar)
        val iconoEliminar: ImageView = itemView.findViewById(R.id.iconoEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return ViewHolder(view)
    }
    fun actualizarLista(nuevaLista: List<Socio>) {
        listaSocios = nuevaLista
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val socio = listaSocios[position]
        holder.textoSocio.text = "${socio.nombre} ${socio.apellido}"

        holder.textoSocio.setOnClickListener {
            val intent = Intent(holder.itemView.context, SocioDetailsActivity::class.java).apply {
                putExtra("SOCIO_ID", socio.id)
                putExtra("SOCIO_NOMBRE", socio.nombre)
                putExtra("SOCIO_APELLIDO", socio.apellido)
                // más extras si querés
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.iconoEditar.setOnClickListener {
            onAccionSocio("editar", socio)
        }

        holder.iconoEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea eliminar a ${socio.nombre} ${socio.apellido}?")
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Sí") { _, _ -> onAccionSocio("eliminar", socio) }
                .setCancelable(true)
                .show()
        }
    }

    override fun getItemCount(): Int = listaSocios.size
}