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
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.Socio
import com.example.clubdeportivog3.R

class ExpirationActivity : AppCompatActivity() {

    private lateinit var baseDatos: ClubDeportivoBD
    private lateinit var recyclerViewVencimientos: RecyclerView
    private lateinit var adaptadorVencimientos: AdaptadorVencimientos
    private var nombresVencimientos = mutableListOf<String>()
    private var sociosVencidos = mutableListOf<Socio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expiration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        baseDatos = ClubDeportivoBD(this)
        recyclerViewVencimientos = findViewById(R.id.recyclerViewVencimientos)
        recyclerViewVencimientos.layoutManager = LinearLayoutManager(this)

        cargarVencimientos()

        adaptadorVencimientos = AdaptadorVencimientos(nombresVencimientos) { accion, nombreCompleto ->
            if (accion == "pago") {
                val socio = sociosVencidos.find { "${it.nombre} ${it.apellido}" == nombreCompleto }
                socio?.let {
                    baseDatos.registrarPago(it.id)
                    cargarVencimientos()
                }
            }
        }
        recyclerViewVencimientos.adapter = adaptadorVencimientos

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            val intent = Intent(this, SocioListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun cargarVencimientos() {
        sociosVencidos = baseDatos.obtenerSociosVencenHoy().toMutableList()
        nombresVencimientos.clear()
        nombresVencimientos.addAll(sociosVencidos.map { "${it.nombre} ${it.apellido}" })
        if (::adaptadorVencimientos.isInitialized) {
            adaptadorVencimientos.notifyDataSetChanged()
        }
    }
}