package com.example.clubdeportivog3.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.Actividad
import com.example.clubdeportivog3.model.Socio
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Esta pantalla muestra todos los detalles de un socio: su nombre, DNI, cuota, si está al día con los pagos,
 * y las actividades en las que está inscripto. También podés registrar un pago o inscribirlo en una actividad.
 */
class SocioDetailsActivity : AppCompatActivity() {
    // Variables para guardar la lista de actividades, la base de datos y el socio que estamos viendo
    private lateinit var listaActividades: List<Actividad>
    private lateinit var db: ClubDeportivoBD
    private lateinit var socio: Socio
    private lateinit var activityViews: List<Pair<TextView, Button>> // Lista para los TextViews y botones de actividades

    // Código para identificar la inscripción
    companion object {
        private const val REQUEST_INSCRIPCION = 1
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Esto hace que la app use toda la pantalla, sin dejar bordes negros
        setContentView(R.layout.activity_socio_details) // Carga el diseño de la pantalla (el XML)

        // Ajustamos los márgenes para que los botones y textos no se pisen con la barra de notificaciones o navegación
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Creamos una conexión con la base de datos SQLite
        db = ClubDeportivoBD(this)

        // Sacamos el ID del socio que nos mandaron desde la pantalla anterior
        val socioId = intent.getIntExtra("SOCIO_ID", -1)
        // Si no hay ID válido, cerramos la pantalla porque no sabemos qué socio mostrar
        if (socioId == -1) {
            finish()
            return
        }

        // Sacamos de qué pantalla venimos (por ejemplo, la lista de socios o la de vencimientos)
        val origen = intent.getStringExtra("ORIGEN") ?: "SocioListActivity"

        // Buscamos el socio en la base de datos usando su ID
        val socioObtenido = db.obtenerSocio(socioId)
        // Si no encontramos el socio, cerramos la pantalla
        if (socioObtenido == null) {
            finish()
            return
        }
        socio = socioObtenido // Guardamos el socio para usarlo después

        // Agarramos todos los elementos del diseño (botones y textos) para mostrar la info
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val tvSocioNumero = findViewById<TextView>(R.id.tvSocioNumero)
        val tvDetalles = findViewById<TextView>(R.id.tvDetalles)
        val tvCuota = findViewById<TextView>(R.id.tvCuota)
        val tvEstadoPago = findViewById<TextView>(R.id.tvEstadoPago)
        val btnRegistrarPago = findViewById<Button>(R.id.btnRegistrarPago)
        val btnInscribirActividad = findViewById<Button>(R.id.btnInscribirActividad)
        val btnGenerarCarnet = findViewById<Button>(R.id.btnGenerarCarnet)

        // Creamos una lista con los TextViews y botones para mostrar hasta 5 actividades
        activityViews = listOf(
            findViewById<TextView>(R.id.tvNombreActividad1) to findViewById(R.id.btnRevocar1),
            findViewById<TextView>(R.id.tvNombreActividad2) to findViewById(R.id.btnRevocar2),
            findViewById<TextView>(R.id.tvNombreActividad3) to findViewById(R.id.btnRevocar3),
            findViewById<TextView>(R.id.tvNombreActividad4) to findViewById(R.id.btnRevocar4),
            findViewById<TextView>(R.id.tvNombreActividad5) to findViewById(R.id.btnRevocar5)
        )

        // Cargamos las actividades del socio
        cargarActividades()

        // Mostramos el número de socio (usamos un texto definido en strings.xml)
        tvSocioNumero.text = getString(R.string.socio_numero, socio.id)
        // Mostramos los detalles del socio (nombre, apellido, DNI, correo, teléfono)
        tvDetalles.text = getString(
            R.string.detalle_socio,
            socio.nombre,
            socio.apellido,
            socio.dni,
            socio.correo
                ?: getString(R.string.no_especificado), // Si no hay correo, ponemos "No especificado"
            socio.telefono
        )
        // Mostramos la cuota mensual con formato de dos decimales
        tvCuota.text = "$ ${"%.2f".format(socio.cuota)}"
        // Mostramos si el socio está al día con los pagos
        tvEstadoPago.text =
            if (socio.pagoAlDia) getString(R.string.estado_pago_realizado) else getString(R.string.estado_pago_pendiente)
        // Cambiamos el color del texto según el estado: verde si está al día, rojo si no
        tvEstadoPago.setTextColor(
            if (socio.pagoAlDia) getColor(android.R.color.holo_green_dark) else getColor(android.R.color.holo_red_dark)
        )

        // Botón para volver a la pantalla anterior (lista de socios o vencimientos)
        btnVolver.setOnClickListener {
            val intent = Intent(
                this,
                if (origen == "ExpirationActivity") ExpirationActivity::class.java else SocioListActivity::class.java
            )
            startActivity(intent)
            finish() // Cerramos esta pantalla para no apilarla
        }

        // Botón para registrar un pago
        btnRegistrarPago.setOnClickListener {
            // Mostramos un diálogo para confirmar el pago
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirmar_registrar_pago)) // Preguntamos si está seguro
                .setNegativeButton(getString(R.string.boton_no)) { _, _ -> } // Si dice "No", no hacemos nada
                .setPositiveButton(getString(R.string.boton_si)) { _, _ -> // Si dice "Sí", seguimos
                    // Registramos el pago en la base de datos
                    val actualizado = db.registrarPago(socio.id)
                    if (actualizado) {
                        // Actualizamos la UI para mostrar que está al día
                        tvEstadoPago.text = getString(R.string.estado_pago_realizado)
                        tvEstadoPago.setTextColor(getColor(android.R.color.holo_green_dark))
                        // Vamos a una pantalla de confirmación
                        val intent = Intent(this, PaymentRegisteredActivity::class.java)
                        startActivity(intent)
                        finish() // Cerramos esta pantalla
                    } else {
                        // Si algo salió mal, mostramos un error
                        AlertDialog.Builder(this)
                            .setMessage(getString(R.string.error_registrar_pago))
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
                .setCancelable(true) // El diálogo se puede cerrar tocando afuera
                .show()
        }

        // Botón para generar Carnet
        btnGenerarCarnet.setOnClickListener {
            mostrarCarnet()
        }

        // Botón para inscribir al socio en una actividad
        btnInscribirActividad.setOnClickListener {
            // Mandamos el ID y nombre del socio a la pantalla de inscripción
            val intent = Intent(this, RegisterInActivityActivity::class.java).apply {
                putExtra("socio_numero", socio.id)
                putExtra("socio_nombre", "${socio.nombre} ${socio.apellido}")
            }
            startActivityForResult(
                intent,
                REQUEST_INSCRIPCION
            ) // Usamos startActivityForResult para detectar la inscripción
        }
    }

    // Método para cargar y mostrar las actividades del socio
    private fun cargarActividades() {
        // Sacamos las actividades en las que está inscripto este socio
        listaActividades = db.obtenerActividadesSocio(socio.id)
        // Recorremos la lista de actividades y las mostramos
        activityViews.forEachIndexed { index, (tvNombre, btnRevocar) ->
            if (index < listaActividades.size) {
                // Si hay una actividad en esta posición, la mostramos
                val actividad = listaActividades[index]
                tvNombre.visibility = View.VISIBLE // Hacemos visible el texto
                btnRevocar.visibility = View.VISIBLE // Hacemos visible el botón
                tvNombre.text =
                    "${actividad.nombre}\n${actividad.descripcion}" // Mostramos nombre y descripción
                // Configuramos el botón para revocar la inscripción
                btnRevocar.setOnClickListener {
                    // Mostramos un diálogo para confirmar
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.confirmar_revocar_inscripcion))
                        .setNegativeButton(getString(R.string.boton_no)) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(getString(R.string.boton_si)) { _, _ ->
                            // Eliminamos la inscripción en la base de datos
                            val eliminado = db.eliminarInscripcionSocio(socio.id, actividad.id)
                            if (eliminado) {
                                // Actualizamos la UI y mostramos confirmación
                                tvNombre.text = getString(R.string.inscripcion_revocada)
                                btnRevocar.visibility = View.GONE
                                val intent = Intent(this, DeletedInscriptionActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Si algo salió mal, mostramos un error
                                AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.error_revocar_inscripcion))
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                        .setCancelable(true)
                        .show()
                }
            } else {
                // Si no hay actividad, ocultamos el texto y el botón
                tvNombre.visibility = View.GONE
                btnRevocar.visibility = View.GONE
            }
        }
    }

    // Método para manejar el resultado de la inscripción
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Si venimos de la inscripción y fue exitosa, recargamos las actividades
        if (requestCode == REQUEST_INSCRIPCION && resultCode == RESULT_OK && data?.getBooleanExtra(
                "INSCRIPCION_REALIZADA",
                false
            ) == true
        ) {
            cargarActividades()
        }
    }

    // MÉTODO PARA GENERAR EL CARNET DE SOCIO
    private fun mostrarCarnet() {
        // Primero verificamos si ya tiene carnet entregado
        if (socio.carnetEntregado) {
            // Si ya tiene carnet, mostramos un mensaje y no hacemos nada más
            // Opción 1: Mantener el Toast (como en tu código original)
            //Toast.makeText(this, "El socio ya tiene un carnet entregado", Toast.LENGTH_SHORT).show()

            // Opción 2: Usar AlertDialog para consistencia (descomentar si preferís esto)
            AlertDialog.Builder(this)
                .setTitle("Carnet ya entregado")
                .setMessage("El socio ya tiene un carnet entregado.")
                .setPositiveButton("Aceptar") { _, _ -> } // Cierra el diálogo
                .setCancelable(true)
                .show()

            return
        }

        // Verificamos si ha pagado la cuota completa
        val CUOTA_FIJA = 15000.0
        if (socio.cuota < CUOTA_FIJA) {
            // Calculamos la deuda
            val deuda = CUOTA_FIJA - socio.cuota
            // Mostramos un AlertDialog en lugar del Toast
            AlertDialog.Builder(this)
                .setTitle("Pago incompleto")
                .setMessage("No se puede generar el carnet. El socio adeuda $${"%.2f".format(deuda)}. Debe abonar la totalidad de la cuota ($${CUOTA_FIJA}).")
                .setPositiveButton("Aceptar") { _, _ -> } // Cierra el diálogo
                .setCancelable(true) // Permite cerrar tocando afuera
                .show()
            return
        }

        // Si no tiene carnet y ha pagado la cuota, procedemos a generar uno
        // Crear un diálogo personalizado
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_carnet_socio, null)
        dialogBuilder.setView(dialogView)

        // Establecer los datos del socio en el carnet
        val tvNombre = dialogView.findViewById<TextView>(R.id.tvNombreCarnet)
        val tvDNI = dialogView.findViewById<TextView>(R.id.tvDNICarnet)
        val tvNumeroSocio = dialogView.findViewById<TextView>(R.id.tvNumeroSocioCarnet)
        val tvFechaEmision = dialogView.findViewById<TextView>(R.id.tvFechaEmisionCarnet)

        tvNombre.text = "${socio.nombre} ${socio.apellido}"
        tvDNI.text = "DNI: ${socio.dni}"
        tvNumeroSocio.text = "Socio Nº ${socio.id}"

        // Obtener la fecha actual
        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        tvFechaEmision.text = "Emitido: $fechaActual"

        // Añadir botones
        dialogBuilder.setPositiveButton("Imprimir") { dialog, _ ->
            // Por ejemplo, capturando la vista como imagen
            dialog.dismiss()

            // Marcar carnet como entregado
            socio.carnetEntregado = true
            val db = ClubDeportivoBD(this)
            db.actualizarSocio(socio)
            Toast.makeText(this, "Carnet generado y marcado como entregado", Toast.LENGTH_SHORT)
                .show()
        }

        dialogBuilder.setNegativeButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }

        // Mostrar el diálogo
        dialogBuilder.show()
    }
}