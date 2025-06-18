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

/**
 * Adaptador para mostrar la lista de No Socios en un RecyclerView.
 * Gestiona la visualización de cada elemento de la lista y maneja las acciones
 * de editar y eliminar para cada No Socio.
 *
 * @param listaNoSocios Lista de objetos NoSocio que se mostrarán
 * @param onAccionNoSocio Función lambda que se ejecuta cuando se selecciona una acción sobre un NoSocio
 */

class AdaptadorNoSocios(
    private var listaNoSocios: List<NoSocio>,
    private val onAccionNoSocio: (accion: String, noSocio: NoSocio) -> Unit
) : RecyclerView.Adapter<AdaptadorNoSocios.ViewHolder>() {

    /**
     * Clase ViewHolder que contiene las referencias a las vistas de cada elemento
     * de la lista de No Socios.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconoNoSocio: ImageView = itemView.findViewById(R.id.iconoSocio)
        val textoNoSocio: TextView = itemView.findViewById(R.id.textoSocio)
        val iconoEditar: ImageView = itemView.findViewById(R.id.iconoEditar)
        val iconoEliminar: ImageView = itemView.findViewById(R.id.iconoEliminar)
    }

    /**
     * Crea un nuevo ViewHolder inflando el layout correspondiente.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return ViewHolder(view)
    }

    /**
     * Actualiza la lista de No Socios y notifica al adaptador para refrescar la vista.
     *
     * @param nuevaLista Nueva lista de No Socios a mostrar
     */

    fun actualizarLista(nuevaLista: List<NoSocio>) {
        listaNoSocios = nuevaLista
        notifyDataSetChanged()
    }

    /**
     * Vincula los datos de un No Socio con las vistas del ViewHolder.
     * Configura también los listeners para las acciones de cada elemento.
     */

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

        // Configurar acción para el botón de editar
        holder.iconoEditar.setOnClickListener {
            onAccionNoSocio("editar", noSocio)
        }

        // Configurar acción para el botón de eliminar, con diálogo de confirmación
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