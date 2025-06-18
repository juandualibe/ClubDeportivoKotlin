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

class AdaptadorActividades(
    private val listaActividades: List<Actividad>,
    private val onAccionActividad: (accion: String, actividad: Actividad) -> Unit
) : RecyclerView.Adapter<AdaptadorActividades.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre: TextView = itemView.findViewById(R.id.textoNombre)
        val textoDescripcion: TextView = itemView.findViewById(R.id.textoDescripcion)
        val iconoEditar: ImageView = itemView.findViewById(R.id.iconoEditar)
        val iconoEliminar: ImageView = itemView.findViewById(R.id.iconoEliminar)
        val contenedorTexto: LinearLayout = itemView.findViewById(R.id.contenedorTexto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = listaActividades[position]
        holder.apply {
            textoNombre.text = actividad.nombre
            textoDescripcion.text = "${actividad.descripcion}\nCupo: ${actividad.cupoMaximo}"

            contenedorTexto.setOnClickListener {
                val intent = Intent(itemView.context, ActividadDetailsActivity::class.java).apply {
                    putExtra("ACTIVIDAD_ID", actividad.id)
                    putExtra("ACTIVIDAD_NOMBRE", actividad.nombre)
                    putExtra("ACTIVIDAD_DESCRIPCION", actividad.descripcion)
                    putExtra("ACTIVIDAD_NUMERO", actividad.id)
                }
                itemView.context.startActivity(intent)
            }
            iconoEditar.setOnClickListener {
                onAccionActividad("editar", actividad)
            }
            iconoEliminar.setOnClickListener {
                onAccionActividad("eliminar", actividad)
            }
        }
    }

    override fun getItemCount(): Int = listaActividades.size
}