package com.example.clubdeportivog3.activities

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

class SocioDetailsActivity : AppCompatActivity() {

    private lateinit var listaActividades: List<Actividad>
    private lateinit var db: ClubDeportivoBD
    private lateinit var socio: Socio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socio_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ClubDeportivoBD(this)

        val socioId = intent.getIntExtra("SOCIO_ID", -1)
        if (socioId == -1) {
            finish()
            return
        }

        val origen = intent.getStringExtra("ORIGEN") ?: "SocioListActivity"

        val socioObtenido = db.obtenerSocio(socioId)
        if (socioObtenido == null) {
            finish()
            return
        }
        socio = socioObtenido

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val tvSocioNumero = findViewById<TextView>(R.id.tvSocioNumero)
        val tvDetalles = findViewById<TextView>(R.id.tvDetalles)
        val tvCuota = findViewById<TextView>(R.id.tvCuota)
        val tvEstadoPago = findViewById<TextView>(R.id.tvEstadoPago)
        val btnRegistrarPago = findViewById<Button>(R.id.btnRegistrarPago)
        val btnInscribirActividad = findViewById<Button>(R.id.btnInscribirActividad)

        listaActividades = db.obtenerActividadesSocio(socio.id)

        // Usar strings.xml con placeholders
        tvSocioNumero.text = getString(R.string.socio_numero, socio.id)
        tvDetalles.text = getString(
            R.string.detalle_socio,
            socio.nombre,
            socio.apellido,
            socio.dni,
            socio.correo ?: getString(R.string.no_especificado),
            socio.telefono
        )
        tvCuota.text = "$ ${"%.2f".format(socio.cuota)}"
        tvEstadoPago.text = if (socio.pagoAlDia) getString(R.string.estado_pago_realizado) else getString(R.string.estado_pago_pendiente)
        tvEstadoPago.setTextColor(
            if (socio.pagoAlDia) getColor(android.R.color.holo_green_dark)
            else getColor(android.R.color.holo_red_dark)
        )

        btnVolver.setOnClickListener {
            val intent = Intent(
                this,
                if (origen == "ExpirationActivity") ExpirationActivity::class.java else SocioListActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        btnRegistrarPago.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirmar_registrar_pago))
                .setNegativeButton(getString(R.string.boton_no)) { _, _ -> }
                .setPositiveButton(getString(R.string.boton_si)) { _, _ ->
                    val actualizado = db.registrarPago(socio.id)
                    if (actualizado) {
                        tvEstadoPago.text = getString(R.string.estado_pago_realizado)
                        tvEstadoPago.setTextColor(getColor(android.R.color.holo_green_dark))
                        val intent = Intent(this, PaymentRegisteredActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        AlertDialog.Builder(this)
                            .setMessage(getString(R.string.error_registrar_pago))
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
                .setCancelable(true)
                .show()
        }

        btnInscribirActividad.setOnClickListener {
            val intent = Intent(this, RegisterInActivityActivity::class.java).apply {
                putExtra("socio_numero", socio.id)
                putExtra("socio_nombre", "${socio.nombre} ${socio.apellido}")
            }
            startActivity(intent)
        }

        val activityViews = listOf(
            findViewById<TextView>(R.id.tvNombreActividad1) to findViewById<Button>(R.id.btnRevocar1),
            findViewById<TextView>(R.id.tvNombreActividad2) to findViewById<Button>(R.id.btnRevocar2),
            findViewById<TextView>(R.id.tvNombreActividad3) to findViewById<Button>(R.id.btnRevocar3),
            findViewById<TextView>(R.id.tvNombreActividad4) to findViewById<Button>(R.id.btnRevocar4),
            findViewById<TextView>(R.id.tvNombreActividad5) to findViewById<Button>(R.id.btnRevocar5)
        )

        activityViews.forEachIndexed { index, (tvNombre, btnRevocar) ->
            if (index < listaActividades.size) {
                val actividad = listaActividades[index]
                tvNombre.visibility = View.VISIBLE
                btnRevocar.visibility = View.VISIBLE

                tvNombre.text = "${actividad.nombre}\n${actividad.descripcion}"
                btnRevocar.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.confirmar_revocar_inscripcion))
                        .setNegativeButton(getString(R.string.boton_no)) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(getString(R.string.boton_si)) { _, _ ->
                            val eliminado = db.eliminarInscripcionSocio(socio.id, actividad.id)
                            if (eliminado) {
                                tvNombre.text = getString(R.string.inscripcion_revocada)
                                btnRevocar.visibility = View.GONE
                                val intent = Intent(this, DeletedInscriptionActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
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
                tvNombre.visibility = View.GONE
                btnRevocar.visibility = View.GONE
            }
        }
    }
}