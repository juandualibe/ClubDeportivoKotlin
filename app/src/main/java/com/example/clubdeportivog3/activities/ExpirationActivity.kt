package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog3.data.AdaptadorVencimientos
import com.example.clubdeportivog3.R

class ExpirationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expiration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar RecyclerView
        val recyclerViewVencimientos = findViewById<RecyclerView>(R.id.recyclerViewVencimientos)
        recyclerViewVencimientos.layoutManager = LinearLayoutManager(this)

        // Lista de socios con vencimientos (ejemplo)
        val listaVencimientos = listOf(
            "Juan Pérez",
            "María Gómez",
            "Carlos López",
            "Ana Martínez"
        )

        // Configurar adaptador
        val adaptadorVencimientos = AdaptadorVencimientos(listaVencimientos) { accion, socio ->
            when (accion) {
                "pago" -> {
                    // No es necesario manejar el pago aquí, ya que se maneja en AdaptadorVencimientos
                }
                "eliminar" -> {
                    // No es necesario manejar la eliminación aquí, ya que se maneja en AdaptadorVencimientos
                }
            }
        }
        recyclerViewVencimientos.adapter = adaptadorVencimientos

        // Botón Volver
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            val intent = Intent(this, SocioListActivity::class.java)
            startActivity(intent)
            finish() // Cierra ExpirationActivity
        }
    }
}