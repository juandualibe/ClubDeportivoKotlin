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
import com.example.clubdeportivog3.activities.NoSocioDetailsActivity
import com.example.clubdeportivog3.model.NoSocio

class AdaptadorNoSocios(
    private var listaNoSocios: List<NoSocio>,
    private val onAccionNoSocio: (accion: String, noSocio: NoSocio) -> Unit
) : RecyclerView.Adapter<AdaptadorNoSocios.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconoNoSocio: ImageView = itemView.findViewById(R.id.iconoSocio)
        val textoNoSocio: TextView = itemView.findViewById(R.id.textoSocio)
        val iconoEditar: ImageView = itemView.findViewById(R.id.iconoEditar)
        val iconoEliminar: ImageView = itemView.findViewById(R.id.iconoEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return ViewHolder(view)
    }
    
    fun actualizarLista(nuevaLista: List<NoSocio>) {
        listaNoSocios = nuevaLista
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noSocio = listaNoSocios[position]
        holder.textoNoSocio.text = "${noSocio.nombre} ${noSocio.apellido}"

        // Click en texto para abrir detalles
        holder.textoNoSocio.setOnClickListener {
            val intent = Intent(holder.itemView.context, NoSocioDetailsActivity::class.java).apply {
                putExtra("NO_SOCIO_ID", noSocio.id)
                putExtra("NO_SOCIO_NOMBRE", noSocio.nombre)
                putExtra("NO_SOCIO_APELLIDO", noSocio.apellido)
                putExtra("NO_SOCIO_DNI", noSocio.dni)
                putExtra("NO_SOCIO_CORREO", noSocio.correo)
                putExtra("NO_SOCIO_TELEFONO", noSocio.telefono)
                putExtra("NO_SOCIO_PAGO_DIARIO", noSocio.pagoDiario)
                putExtra("NO_SOCIO_APTO_FISICO", noSocio.aptoFisico)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.iconoEditar.setOnClickListener {
            onAccionNoSocio("editar", noSocio)
        }

        holder.iconoEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea eliminar a ${noSocio.nombre} ${noSocio.apellido}?")
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Sí") { _, _ -> onAccionNoSocio("eliminar", noSocio) }
                .setCancelable(true)
                .show()
        }
    }

    override fun getItemCount(): Int = listaNoSocios.size
}