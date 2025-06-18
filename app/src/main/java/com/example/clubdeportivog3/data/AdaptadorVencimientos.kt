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
import com.example.clubdeportivog3.activities.DeletedSocioActivity
import com.example.clubdeportivog3.activities.PaymentRegisteredActivity
import com.example.clubdeportivog3.activities.SocioDetailsActivity

/**
 * Adaptador para mostrar socios con pagos vencidos.
 */
class AdaptadorVencimientos(
    private val listaVencimientos: MutableList<String>, // Lista de nombres de socios vencidos
    private val onAccionVencimiento: (accion: String, socio: String) -> Unit // Callback para acciones
) : RecyclerView.Adapter<AdaptadorVencimientos.ViewHolder>() {

    // Clase para cada item de la lista
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconoSocio: ImageView = itemView.findViewById(R.id.iconoSocio)
        val textoSocio: TextView = itemView.findViewById(R.id.textoSocio)
        val iconoPago: TextView = itemView.findViewById(R.id.iconoPago)
        val iconoEliminar: ImageView = itemView.findViewById(R.id.iconoEliminar)
    }

    // Crea la vista de cada item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vencimiento, parent, false)
        return ViewHolder(view)
    }

    // Llena los datos de cada item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val socio = listaVencimientos[position]
        holder.textoSocio.text = socio // Muestra el nombre del socio

        // Al tocar el texto, va a los detalles del socio
        holder.textoSocio.setOnClickListener {
            val intent = Intent(holder.itemView.context, SocioDetailsActivity::class.java).apply {
                putExtra("SOCIO_NOMBRE", socio)
                putExtra("SOCIO_NUMERO", position + 100) // ID ficticio
                putExtra("ORIGEN", "ExpirationActivity")
            }
            holder.itemView.context.startActivity(intent)
        }

        // Botón para registrar pago con confirmación
        holder.iconoPago.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea registrar un pago a este socio?")
                .setNegativeButton("No") { _, _ -> }
                .setPositiveButton("Sí") { _, _ ->
                    onAccionVencimiento("pago", socio)
                }
                .setCancelable(true)
                .show()
        }

        // Botón para eliminar socio con confirmación
        holder.iconoEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("¿Está seguro que desea eliminar este socio?")
                .setNegativeButton("No") { _, _ -> }
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(holder.itemView.context, DeletedSocioActivity::class.java)
                    holder.itemView.context.startActivity(intent)
                }
                .setCancelable(true)
                .show()
        }
    }

    // Devuelve la cantidad de items
    override fun getItemCount(): Int = listaVencimientos.size
}