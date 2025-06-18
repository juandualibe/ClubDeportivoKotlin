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

/**
 * Pantalla que muestra los detalles de una actividad y sus inscriptos.
 */
class ActividadDetailsActivity : AppCompatActivity() {
    private lateinit var dbHelper: ClubDeportivoBD // Base de datos
    private var actividadId: Int = 0 // ID de la actividad

    data class Inscripto(val id: Int, val nombre: String, val esSocio: Boolean) // Clase para manejar inscriptos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.activity_actividad_details) // Carga el diseño

        // Ajusta márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ClubDeportivoBD(this) // Conexión a la base de datos
        actividadId = intent.getIntExtra("ACTIVIDAD_NUMERO", 0) // Saca el ID de la actividad

        // Agarra los elementos del diseño
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

        // Busca la actividad en la base de datos
        val actividad = dbHelper.obtenerActividadPorId(actividadId)
        if (actividad == null) {
            tvActividadNumero.text = getString(R.string.actividad_no_encontrada) // Muestra error si no existe
            tvDetalles.text = ""
            tvInscriptos.text = ""
            inscriptoViews.forEach { (tv, btn) -> tv.visibility = View.GONE; btn.visibility = View.GONE }
            return
        }

        // Muestra los detalles de la actividad
        tvActividadNumero.text = getString(R.string.actividad_numero, actividad.id)
        tvDetalles.text = getString(R.string.detalles_actividad, actividad.nombre, actividad.descripcion)

        // Muestra los inscriptos y su cantidad
        val inscriptos = obtenerInscriptos(actividadId)
        tvInscriptos.text = getString(R.string.lista_inscriptos, inscriptos.size, actividad.cupoMaximo)

        // Configura hasta 5 inscriptos con botones para revocar
        inscriptoViews.forEachIndexed { index, (tvNombre, btnRevocar) ->
            if (index < inscriptos.size) {
                val inscripto = inscriptos[index]
                tvNombre.text = inscripto.nombre
                tvNombre.visibility = View.VISIBLE
                btnRevocar.visibility = View.VISIBLE
                btnRevocar.setOnClickListener {
                    // Confirma antes de revocar inscripción
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.confirmar_revocar_inscripcion, inscripto.nombre))
                        .setNegativeButton(getString(R.string.btn_no)) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(getString(R.string.btn_si)) { dialog, _ ->
                            // Revoca inscripción según si es socio o no
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
                                finish() // Cierra la pantalla
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
                tvNombre.visibility = View.GONE // Oculta si no hay inscripto
                btnRevocar.visibility = View.GONE
            }
        }

        // Botón para volver a la lista de actividades
        btnVolver.setOnClickListener {
            startActivity(Intent(this, ActividadesListActivity::class.java))
            finish()
        }
    }

    // Saca los inscriptos (socios y no socios) de la base de datos
    private fun obtenerInscriptos(actividadId: Int): List<Inscripto> {
        val inscriptos = mutableListOf<Inscripto>()
        val db = dbHelper.readableDatabase

        // Busca socios inscriptos
        val cursorSocios = db.rawQuery("""
            SELECT s.id, s.nombre
            FROM inscripciones_socios i
            JOIN socios s ON i.socio_id = s.id
            WHERE i.actividad_id = ?
        """.trimIndent(), arrayOf(actividadId.toString()))
        while (cursorSocios.moveToNext()) {
            inscriptos.add(Inscripto(cursorSocios.getInt(0), cursorSocios.getString(1), true))
        }
        cursorSocios.close()

        // Busca no socios inscriptos
        val cursorNoSocios = db.rawQuery("""
            SELECT n.id, n.nombre
            FROM inscripciones_nosocios i
            JOIN nosocios n ON i.no_socio_id = n.id
            WHERE i.actividad_id = ?
        """.trimIndent(), arrayOf(actividadId.toString()))
        while (cursorNoSocios.moveToNext()) {
            inscriptos.add(Inscripto(cursorNoSocios.getInt(0), cursorNoSocios.getString(1), false))
        }
        cursorNoSocios.close()

        return inscriptos
    }
}