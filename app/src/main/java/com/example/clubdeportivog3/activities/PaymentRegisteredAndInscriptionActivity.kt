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

class PaymentRegisteredAndInscriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.payment_registed_and_inscription)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener datos del Intent
        val noSocioNumero = intent.getIntExtra("NO_SOCIO_NUMERO", -1)
        val noSocioNombre = intent.getStringExtra("NO_SOCIO_NOMBRE") ?: "Desconocido"

        // Configurar el texto de confirmación
        val tvConfirmacion = findViewById<TextView>(R.id.textView)
        tvConfirmacion.text = "Pago registrado e inscripción realizada"

        // Botón Aceptar
        val btnAceptar = findViewById<Button>(R.id.buttonIngresar)
        btnAceptar.setOnClickListener {
            val intent = Intent(this, NoSocioDetailsActivity::class.java).apply {
                putExtra("NO_SOCIO_NOMBRE", noSocioNombre)
                putExtra("NO_SOCIO_NUMERO", noSocioNumero)
            }
            startActivity(intent)
            finish()
        }
    }
}