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
import com.example.clubdeportivog3.data.AdaptadorRegisterActividades
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD

class RegisterInActivityActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorRegisterActividades

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_in_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener socio_numero y socio_nombre del Intent
        val socioNumero = intent.getIntExtra("socio_numero", -1)
        val socioNombre = intent.getStringExtra("socio_nombre") ?: "Desconocido"

        val db = ClubDeportivoBD(this)

        // Referencias a los elementos de la UI
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        recyclerView = findViewById(R.id.recyclerViewActividades)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Convertir List a MutableList
        val actividades = db.obtenerActividades().toMutableList()
        recyclerView.contentDescription = getString(R.string.activity_list_description)
        adaptador = AdaptadorRegisterActividades(actividades, socioNumero, socioNombre)
        recyclerView.adapter = adaptador

        // Botón Volver
        btnVolver.setOnClickListener {
            val intent = Intent(this, SocioDetailsActivity::class.java).apply {
                putExtra("SOCIO_NOMBRE", socioNombre)
                putExtra("SOCIO_NUMERO", socioNumero)
            }
            startActivity(intent)
            finish()
        }
    }
}