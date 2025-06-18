package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivog3.R

class AddedRegistrationAceptedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.added_registration_acepted)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener datos del Intent para socio o no socio
        val socioNumero = intent.getIntExtra("SOCIO_ID", -1)
        val socioNombre = intent.getStringExtra("SOCIO_NOMBRE").orEmpty()

        val noSocioNumero = intent.getIntExtra("NO_SOCIO_ID", -1)
        val noSocioNombre = intent.getStringExtra("NO_SOCIO_NOMBRE").orEmpty()

        // Configurar el texto de confirmación usando recurso string
        val tvConfirmacion = findViewById<TextView>(R.id.textView)
        tvConfirmacion.text = getString(R.string.register_made) // "Inscripción realizada"

        // Botón Aceptar
        val btnAceptar = findViewById<Button>(R.id.buttonIngresar)
        btnAceptar.setOnClickListener {
            when {
                socioNumero != -1 -> {
                    val intent = Intent(this, SocioDetailsActivity::class.java).apply {
                        putExtra("SOCIO_NOMBRE", socioNombre)
                        putExtra("SOCIO_ID", socioNumero)
                    }
                    startActivity(intent)
                    finish()
                }
                noSocioNumero != -1 -> {
                    val intent = Intent(this, NoSocioDetailsActivity::class.java).apply {
                        putExtra("NO_SOCIO_NOMBRE", noSocioNombre)
                        putExtra("NO_SOCIO_ID", noSocioNumero)
                    }
                    startActivity(intent)
                    finish()
                }
                else -> {
                    finish()
                }
            }
        }
    }
}