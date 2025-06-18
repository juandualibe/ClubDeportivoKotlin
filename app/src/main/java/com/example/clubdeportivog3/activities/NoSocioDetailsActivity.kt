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
import com.example.clubdeportivog3.model.NoSocio


class NoSocioDetailsActivity : AppCompatActivity() {

    private lateinit var db: ClubDeportivoBD
    private lateinit var noSocio: NoSocio
    private lateinit var listaActividades: List<Actividad>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_no_socio_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ClubDeportivoBD(this)

        val noSocioId = intent.getIntExtra("NO_SOCIO_ID", -1)
        if (noSocioId == -1) {
            finish()
            return
        }

        val origen = intent.getStringExtra("ORIGEN") ?: "noSocioListActivity"

        val noSocioObtenido = db.obtenerNoSocio(noSocioId)
        if (noSocioObtenido == null) {
            finish()
            return
        }
        noSocio = noSocioObtenido

        // Referencias a los elementos del layout
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val tvNoSocioNumero = findViewById<TextView>(R.id.tvSocioNumero)
        val tvDetalles = findViewById<TextView>(R.id.tvDetalles)
        val btnInscribirActividad = findViewById<Button>(R.id.btnInscribirActividad)

        // Usar strings.xml con placeholders
        tvNoSocioNumero.text = getString(R.string.socio_numero, noSocio.id)
        tvDetalles.text = getString(
            R.string.detalle_socio,
            noSocio.nombre,
            noSocio.apellido,
            noSocio.dni,
            noSocio.correo ?: getString(R.string.no_especificado),
            noSocio.telefono
        )



        // Obtener actividades del no socio
        listaActividades = db.obtenerActividadesNoSocio(noSocioId)



        btnInscribirActividad.setOnClickListener {
            val intent = Intent(this, RegisterInActivityNoSocioActivity::class.java).apply {
                putExtra("no_socio_numero", noSocio.id)
                putExtra("no_socio_nombre", "${noSocio.nombre} ${noSocio.apellido}")
            }
            startActivity(intent)
        }
        btnVolver.setOnClickListener {
            val intent = Intent(
                this,
                if (origen == "ExpirationActivity") ExpirationActivity::class.java else NoSocioListActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        btnInscribirActividad.setOnClickListener {
            val intent = Intent(this, RegisterInActivityNoSocioActivity::class.java).apply {
                putExtra("no_socio_numero", noSocio.id)
                putExtra("no_socio_nombre", "${noSocio.nombre} ${noSocio.apellido}")
            }
            startActivity(intent)
        }

        // Mostrar actividades en la UI
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
                        .setMessage("¿Está seguro que desea revocar la inscripción del no socio en esta actividad?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Sí") { _, _ ->
                            val bd = ClubDeportivoBD(this)
                            val exito = bd.eliminarInscripcionNoSocio(noSocio.id, actividad.id)
                            bd.close()
                            if (exito) {
                                tvNombre.text = "Inscripción revocada"
                                btnRevocar.visibility = View.GONE
                                val intent = Intent(this, DeletedInscriptionActivity::class.java).apply {
                                    putExtra("ORIGEN", "NoSocioDetailsActivity")
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                AlertDialog.Builder(this)
                                    .setMessage("Error al revocar la inscripción.")
                                    .setPositiveButton("Ok", null)
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