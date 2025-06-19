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

/**
 * Pantalla que muestra los detalles de un no socio y sus actividades.
 */
class NoSocioDetailsActivity : AppCompatActivity() {
    private lateinit var db: ClubDeportivoBD // Base de datos
    private lateinit var noSocio: NoSocio // No socio actual
    private lateinit var listaActividades: List<Actividad> // Actividades del no socio
    private lateinit var activityViews: List<Pair<TextView, Button>> // Lista para los TextViews y botones de actividades

    // Código para identificar la inscripción
    companion object {
        private const val REQUEST_INSCRIPCION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.activity_no_socio_details) // Carga el diseño

        // Ajusta márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ClubDeportivoBD(this) // Conexión a la base de datos

        // Sacamos el ID del no socio
        val noSocioId = intent.getIntExtra("NO_SOCIO_ID", -1)
        if (noSocioId == -1) {
            finish() // Cerramos si no hay ID
            return
        }

        // Sacamos de dónde venimos
        val origen = intent.getStringExtra("ORIGEN") ?: "NoSocioListActivity"

        // Buscamos el no socio en la base de datos
        val noSocioObtenido = db.obtenerNoSocio(noSocioId)
        if (noSocioObtenido == null) {
            finish() // Cerramos si no existe
            return
        }
        noSocio = noSocioObtenido

        // Agarramos los elementos del diseño
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val tvNoSocioNumero = findViewById<TextView>(R.id.tvSocioNumero)
        val tvDetalles = findViewById<TextView>(R.id.tvDetalles)
        val btnInscribirActividad = findViewById<Button>(R.id.btnInscribirActividad)

        // Lista de TextViews y botones para mostrar hasta 5 actividades
        activityViews = listOf(
            findViewById<TextView>(R.id.tvNombreActividad1) to findViewById<Button>(R.id.btnRevocar1),
            findViewById<TextView>(R.id.tvNombreActividad2) to findViewById<Button>(R.id.btnRevocar2),
            findViewById<TextView>(R.id.tvNombreActividad3) to findViewById<Button>(R.id.btnRevocar3),
            findViewById<TextView>(R.id.tvNombreActividad4) to findViewById<Button>(R.id.btnRevocar4),
            findViewById<TextView>(R.id.tvNombreActividad5) to findViewById<Button>(R.id.btnRevocar5)
        )

        // Cargamos las actividades del no socio
        cargarActividades()

        // Mostramos la info del no socio
        tvNoSocioNumero.text = getString(R.string.socio_numero, noSocio.id)
        tvDetalles.text = getString(
            R.string.detalle_socio,
            noSocio.nombre,
            noSocio.apellido,
            noSocio.dni,
            noSocio.correo ?: getString(R.string.no_especificado),
            noSocio.telefono
        )

        // Botón para inscribir en una actividad
        btnInscribirActividad.setOnClickListener {
            val intent = Intent(this, RegisterInActivityNoSocioActivity::class.java).apply {
                putExtra("no_socio_numero", noSocio.id)
                putExtra("no_socio_nombre", "${noSocio.nombre} ${noSocio.apellido}")
            }
            startActivityForResult(intent, REQUEST_INSCRIPCION) // Usamos startActivityForResult
        }

        // Botón para volver a la lista o vencimientos
        btnVolver.setOnClickListener {
            val intent = Intent(
                this,
                if (origen == "ExpirationActivity") ExpirationActivity::class.java else NoSocioListActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    // Método para cargar y mostrar las actividades del no socio
    private fun cargarActividades() {
        // Sacamos las actividades del no socio
        listaActividades = db.obtenerActividadesNoSocio(noSocio.id)
        // Mostramos las actividades
        activityViews.forEachIndexed { index, (tvNombre, btnRevocar) ->
            if (index < listaActividades.size) {
                val actividad = listaActividades[index]
                tvNombre.visibility = View.VISIBLE // Mostramos el texto
                btnRevocar.visibility = View.VISIBLE // Mostramos el botón
                tvNombre.text = "${actividad.nombre}\n${actividad.descripcion}" // Nombre y descripción
                // Botón para revocar inscripción
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
                tvNombre.visibility = View.GONE // Ocultamos si no hay actividad
                btnRevocar.visibility = View.GONE
            }
        }
    }

    // Método para manejar el resultado de la inscripción
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Si venimos de la inscripción y fue exitosa, recargamos las actividades
        if (requestCode == REQUEST_INSCRIPCION && resultCode == RESULT_OK && data?.getBooleanExtra("INSCRIPCION_REALIZADA", false) == true) {
            cargarActividades()
        }
    }
}