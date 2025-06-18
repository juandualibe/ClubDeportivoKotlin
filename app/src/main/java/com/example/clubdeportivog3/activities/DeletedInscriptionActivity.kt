package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivog3.data.ActividadesListActivity
import com.example.clubdeportivog3.R

class DeletedInscriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.deleted_inscription)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el origen del Intent
        val origen = intent.getStringExtra("ORIGEN") ?: "SocioDetailsActivity"

        // Referencia al botón Aceptar
        val btnAceptar = findViewById<Button>(R.id.buttonIngresar)

        // Al presionar Aceptar, regresar a la lista correspondiente
        btnAceptar.setOnClickListener {
            val targetActivity = when (origen) {
                "NoSocioDetailsActivity" -> NoSocioListActivity::class.java
                "ActividadDetailsActivity" -> ActividadesListActivity::class.java
                else -> SocioListActivity::class.java
            }
            val intent = Intent(this, targetActivity)
            startActivity(intent)
            finish() // Cierra DeletedInscriptionActivity
        }
    }
}