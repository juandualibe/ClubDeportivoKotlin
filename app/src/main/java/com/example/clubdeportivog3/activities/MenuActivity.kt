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

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los botones
        val btnSocios = findViewById<Button>(R.id.btnSocios)
        val btnNoSocios = findViewById<Button>(R.id.btnNoSocios)
        val btnActividades = findViewById<Button>(R.id.btnActividades3)
        val btnSalir = findViewById<Button>(R.id.btnSalir)

        // Navegar a SociosActivity
        btnSocios.setOnClickListener {
            val intent = Intent(this, SocioListActivity::class.java)
            startActivity(intent)
        }

        // Navegar a NoSociosActivity
        btnNoSocios.setOnClickListener {
            val intent = Intent(this, NoSocioListActivity::class.java)
            startActivity(intent)
        }

        // Navegar a ActividadesActivity
        btnActividades.setOnClickListener {
            val intent = Intent(this, ActividadesListActivity::class.java)
            startActivity(intent)
        }

        // Cerrar sesión y volver al login
        btnSalir.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish() // Cierra MenuActivity
        }
    }
}