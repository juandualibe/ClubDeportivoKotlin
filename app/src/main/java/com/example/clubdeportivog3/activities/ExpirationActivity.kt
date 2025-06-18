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

/**
 * Pantalla que muestra los socios con pagos vencidos hoy.
 */
class ExpirationActivity : AppCompatActivity() {
    private lateinit var baseDatos: ClubDeportivoBD // Base de datos
    private lateinit var recyclerViewVencimientos: RecyclerView // Lista de vencimientos
    private lateinit var adaptadorVencimientos: AdaptadorVencimientos // Adaptador para la lista
    private var nombresVencimientos = mutableListOf<String>() // Nombres de socios vencidos
    private var sociosVencidos = mutableListOf<Socio>() // Socios vencidos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.activity_expiration) // Carga el diseño

        // Ajusta márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        baseDatos = ClubDeportivoBD(this) // Conexión a la base de datos
        recyclerViewVencimientos = findViewById(R.id.recyclerViewVencimientos)
        recyclerViewVencimientos.layoutManager = LinearLayoutManager(this) // Lista vertical

        cargarVencimientos() // Carga los socios vencidos

        // Configura el adaptador para mostrar vencimientos
        adaptadorVencimientos = AdaptadorVencimientos(nombresVencimientos) { accion, nombreCompleto ->
            if (accion == "pago") {
                // Registra el pago del socio
                val socio = sociosVencidos.find { "${it.nombre} ${it.apellido}" == nombreCompleto }
                socio?.let {
                    baseDatos.registrarPago(it.id)
                    cargarVencimientos() // Actualiza la lista
                }
            }
        }
        recyclerViewVencimientos.adapter = adaptadorVencimientos

        // Botón para volver a la lista de socios
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            val intent = Intent(this, SocioListActivity::class.java)
            startActivity(intent)
            finish() // Cierra esta pantalla
        }
    }

    // Carga los socios que vencen hoy y actualiza la lista
    private fun cargarVencimientos() {
        sociosVencidos = baseDatos.obtenerSociosVencenHoy().toMutableList()
        nombresVencimientos.clear()
        nombresVencimientos.addAll(sociosVencidos.map { "${it.nombre} ${it.apellido}" })
        if (::adaptadorVencimientos.isInitialized) {
            adaptadorVencimientos.notifyDataSetChanged() // Refresca la lista
        }
    }
}