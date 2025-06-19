package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivog3.R

/**
 * Pantalla de confirmación cuando inscribís un socio o no socio en una actividad.
 */
class AddedRegistrationAceptedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.added_registration_acepted) // Carga el diseño

        // Ajusta márgenes para no tapar barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sacamos los datos del Intent
        val socioId = intent.getIntExtra("SOCIO_ID", -1)
        val noSocioId = intent.getIntExtra("NO_SOCIO_NUMERO", -1)
        val socioNombre = intent.getStringExtra("SOCIO_NOMBRE") ?: "Desconocido"
        val noSocioNombre = intent.getStringExtra("NO_SOCIO_NOMBRE") ?: "Desconocido"
        val actividadNombre = intent.getStringExtra("ACTIVIDAD_NOMBRE") ?: "Desconocida"
        val origen = intent.getStringExtra("ORIGEN") ?: "SocioListActivity"

        // Agarramos el botón de aceptar
        val btnAceptar = findViewById<Button>(R.id.buttonIngresar)
        btnAceptar.setOnClickListener {
            // Según el origen, volvemos a la pantalla correspondiente
            when (origen) {
                "SocioDetailsActivity" -> {
                    val intent = Intent(this, SocioDetailsActivity::class.java).apply {
                        putExtra("SOCIO_ID", socioId)
                        putExtra("SOCIO_NOMBRE", socioNombre)
                        putExtra("ORIGEN", "SocioListActivity") // Mantenemos el origen original
                    }
                    setResult(RESULT_OK, Intent().putExtra("INSCRIPCION_REALIZADA", true)) // Mandamos el resultado
                    startActivity(intent)
                }
                "NoSocioDetailsActivity" -> {
                    val intent = Intent(this, NoSocioDetailsActivity::class.java).apply {
                        putExtra("NO_SOCIO_ID", noSocioId)
                        putExtra("NO_SOCIO_NOMBRE", noSocioNombre)
                        putExtra("ORIGEN", "NoSocioListActivity")
                    }
                    setResult(RESULT_OK, Intent().putExtra("INSCRIPCION_REALIZADA", true))
                    startActivity(intent)
                }
                else -> {
                    // Por defecto, volvemos a la lista de socios
                    val intent = Intent(this, SocioListActivity::class.java)
                    startActivity(intent)
                }
            }
            finish() // Cerramos esta pantalla
        }
    }
}