package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivog3.R

class DeletedNoSocioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.deleted_no_user)

        // Ajustar padding para evitar superposición con barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón Aceptar para volver a la lista de NoSocios
        val btnAceptar = findViewById<Button>(R.id.buttonIngresar)
        btnAceptar.setOnClickListener {
            val intent = Intent(this, NoSocioListActivity::class.java)
            startActivity(intent)
            finish() // Cierra esta actividad para que no quede en el back stack
        }
    }
}