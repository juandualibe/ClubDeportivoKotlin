package com.example.clubdeportivog3.data

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
import com.example.clubdeportivog3.activities.DeletedInscriptionActivity

class ActividadDetailsActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDeportivoBD
    private var actividadId: Int = 0

    data class Inscripto(val id: Int, val nombre: String, val esSocio: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividad_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ClubDeportivoBD(this)
        actividadId = intent.getIntExtra("ACTIVIDAD_NUMERO", 0)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val tvActividadNumero = findViewById<TextView>(R.id.tvActividadNumero)
        val tvDetalles = findViewById<TextView>(R.id.tvDetalles)
        val tvInscriptos = findViewById<TextView>(R.id.tvNombreInscripto5)
        val inscriptoViews = listOf(
            findViewById<TextView>(R.id.tvNombreInscripto1) to findViewById<Button>(R.id.btnRevocar1),
            findViewById<TextView>(R.id.tvNombreInscripto2) to findViewById<Button>(R.id.btnRevocar2),
            findViewById<TextView>(R.id.tvNombreInscripto3) to findViewById<Button>(R.id.btnRevocar3),
            findViewById<TextView>(R.id.tvNombreInscripto4) to findViewById<Button>(R.id.btnRevocar4),
            findViewById<TextView>(R.id.tvNombreInscripto5) to findViewById<Button>(R.id.btnRevocar5)
        )

        val actividad = dbHelper.obtenerActividadPorId(actividadId)
        if (actividad == null) {
            tvActividadNumero.text = getString(R.string.actividad_no_encontrada)
            tvDetalles.text = ""
            tvInscriptos.text = ""
            inscriptoViews.forEach { (tv, btn) ->
                tv.visibility = View.GONE
                btn.visibility = View.GONE
            }
            return
        }

        tvActividadNumero.text = getString(R.string.actividad_numero, actividad.id)
        tvDetalles.text = getString(R.string.detalles_actividad, actividad.nombre, actividad.descripcion)

        val inscriptos = obtenerInscriptos(actividadId)
        tvInscriptos.text = getString(R.string.lista_inscriptos, inscriptos.size, actividad.cupoMaximo)

        inscriptoViews.forEachIndexed { index, (tvNombre, btnRevocar) ->
            if (index < inscriptos.size) {
                val inscripto = inscriptos[index]
                tvNombre.text = inscripto.nombre
                tvNombre.visibility = View.VISIBLE
                btnRevocar.visibility = View.VISIBLE

                btnRevocar.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.confirmar_revocar_inscripcion, inscripto.nombre))
                        .setNegativeButton(getString(R.string.btn_no)) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(getString(R.string.btn_si)) { dialog, _ ->
                            val eliminado = if (inscripto.esSocio) {
                                dbHelper.eliminarInscripcionSocio(inscripto.id, actividadId)
                            } else {
                                dbHelper.eliminarInscripcionNoSocio(inscripto.id, actividadId)
                            }
                            if (eliminado) {
                                tvNombre.text = getString(R.string.inscripcion_revocada)
                                btnRevocar.visibility = View.GONE

                                startActivity(Intent(this, DeletedInscriptionActivity::class.java).apply {
                                    putExtra("ORIGEN", "ActividadDetailsActivity")
                                })
                                finish()
                            } else {
                                AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.error_no_eliminar_inscripcion))
                                    .setPositiveButton(getString(R.string.btn_ok), null)
                                    .show()
                            }
                            dialog.dismiss()
                        }
                        .setCancelable(true)
                        .show()
                }
            } else {
                tvNombre.visibility = View.GONE
                btnRevocar.visibility = View.GONE
            }
        }

        btnVolver.setOnClickListener {
            startActivity(Intent(this, ActividadesListActivity::class.java))
            finish()
        }
    }

    private fun obtenerInscriptos(actividadId: Int): List<Inscripto> {
        val inscriptos = mutableListOf<Inscripto>()
        val db = dbHelper.readableDatabase

        val cursorSocios = db.rawQuery("""
            SELECT s.id, s.nombre
            FROM inscripciones_socios i
            JOIN socios s ON i.socio_id = s.id
            WHERE i.actividad_id = ?
        """.trimIndent(), arrayOf(actividadId.toString()))

        while (cursorSocios.moveToNext()) {
            val id = cursorSocios.getInt(0)
            val nombre = cursorSocios.getString(1)
            inscriptos.add(Inscripto(id, nombre, true))
        }
        cursorSocios.close()

        val cursorNoSocios = db.rawQuery("""
            SELECT n.id, n.nombre
            FROM inscripciones_nosocios i
            JOIN nosocios n ON i.no_socio_id = n.id
            WHERE i.actividad_id = ?
        """.trimIndent(), arrayOf(actividadId.toString()))

        while (cursorNoSocios.moveToNext()) {
            val id = cursorNoSocios.getInt(0)
            val nombre = cursorNoSocios.getString(1)
            inscriptos.add(Inscripto(id, nombre, false))
        }
        cursorNoSocios.close()

        return inscriptos
    }
}