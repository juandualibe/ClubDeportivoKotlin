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

        // Obtener el NoSocio actual
        val noSocio = db.obtenerNoSocio(noSocioNumero)

        // Calcular cupo disponible dinámicamente
        val cupoDisponible = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)

        holder.textoNombre.text = actividad.nombre
        holder.textoDescripcion.text = "${actividad.descripcion}\nCupo disponible: $cupoDisponible\nPrecio: $${actividad.monto}"

        holder.btnInscribir.setOnClickListener {
            // Recalcular cupo al momento del click
            val cupoActualizado = db.obtenerCupoDisponible(actividad.id, actividad.cupoMaximo)

            // Verificar si el NoSocio existe
            if (noSocio == null) {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Error")
                    .setMessage("No se pudo encontrar la información del no socio.")
                    .setPositiveButton("Aceptar", null)
                    .show()
                return@setOnClickListener
            }

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

                        // NUEVA VALIDACIÓN: Verificar si tiene apto físico
                    } else if (!noSocio.aptoFisico) {
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Apto físico requerido")
                            .setMessage("El no socio debe presentar el apto físico para inscribirse en actividades.")
                            .setPositiveButton("Aceptar", null)
                            .show()

                    } else {
                        // VALIDACIÓN: Verificar si el pagoDiario del NoSocio coincide con el monto de la actividad
                        if (noSocio.pagoDiario < actividad.monto) {
                            // Mostrar alerta de pago insuficiente
                            AlertDialog.Builder(holder.itemView.context)
                                .setTitle("Pago insuficiente")
                                .setMessage("El no socio debe pagar el monto completo de la actividad: $${actividad.monto}. " +
                                        "Actualmente su pago diario es: $${noSocio.pagoDiario}.\n\n" +
                                        "¿Desea actualizar el pago diario del no socio para inscribirlo?")
                                .setPositiveButton("Actualizar pago") { _, _ ->
                                    // Actualizar el pagoDiario del NoSocio
                                    val noSocioActualizado = NoSocio(
                                        id = noSocio.id,
                                        nombre = noSocio.nombre,
                                        apellido = noSocio.apellido,
                                        dni = noSocio.dni,
                                        correo = noSocio.correo,
                                        telefono = noSocio.telefono,
                                        pagoDiario = actividad.monto,
                                        aptoFisico = noSocio.aptoFisico
                                    )

                                    val exitoActualizacion = db.actualizarNoSocio(noSocioActualizado)
                                    if (exitoActualizacion) {
                                        // Ahora intentar inscribir
                                        inscribirNoSocio(db, holder, position, actividad)
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
                            // El pago es correcto, inscribir directamente
                            inscribirNoSocio(db, holder, position, actividad)
                        }
                    }
                }
                .setCancelable(true)
                .show()
        }

        db.close()
    }

    private fun inscribirNoSocio(db: ClubDeportivoBD, holder: ViewHolder, position: Int, actividad: Actividad) {
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

    override fun getItemCount(): Int = actividades.size
}