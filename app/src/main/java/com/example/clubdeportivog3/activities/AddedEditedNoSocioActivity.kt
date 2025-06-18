package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivog3.R

class AddedEditedNoSocioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.added_edited_no_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencia al botón Aceptar
        val btnAceptar = findViewById<Button>(R.id.buttonIngresar)

        // Al presionar Aceptar, regresar a NoSocioListActivity
        btnAceptar.setOnClickListener {
            val intent = Intent(this, NoSocioListActivity::class.java)
            startActivity(intent)
            finish() // Cierra AddedEditedNoSocioActivity
        }
    }
}