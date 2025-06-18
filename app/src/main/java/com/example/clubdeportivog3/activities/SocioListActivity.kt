package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.AdaptadorSocios
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.Socio

class SocioListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarSocio: Button
    private lateinit var btnVolver: Button
    private lateinit var baseDatos: ClubDeportivoBD
    private lateinit var adaptadorSocios: AdaptadorSocios

    private var origen: String = "MenuActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_list)

        recyclerView = findViewById(R.id.recyclerViewSocios)
        btnAgregarSocio = findViewById(R.id.btnAgregar)
        btnVolver = findViewById(R.id.btnVolver)
        baseDatos = ClubDeportivoBD(this)

        origen = intent.getStringExtra("ORIGEN") ?: "MenuActivity"

        recyclerView.layoutManager = LinearLayoutManager(this)
        adaptadorSocios = AdaptadorSocios(emptyList()) { accion: String, socio: Socio ->
            when (accion) {
                "editar" -> {
                    val intent = Intent(this, AddEditSocioActivity::class.java).apply {
                        putExtra("SOCIO_ID", socio.id)
                    }
                    startActivity(intent)
                }

                "eliminar" -> {
                    val eliminado = baseDatos.eliminarSocio(socio.id)
                    if (eliminado) {
                        // En lugar de Toast, arrancamos DeletedSocioActivity
                        val intent = Intent(this, DeletedSocioActivity::class.java)
                        startActivity(intent)
                        finish()  // Cierra SocioListActivity para evitar volver con back
                    } else {
                        Toast.makeText(this, "No se pudo eliminar el socio", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
        recyclerView.adapter = adaptadorSocios

        btnAgregarSocio.setOnClickListener {
            val intent = Intent(this, AddEditSocioActivity::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(
                this,
                if (origen == "ExpirationActivity") ExpirationActivity::class.java else MenuActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarListaSocios()
    }

    private fun actualizarListaSocios() {
        val nuevosSocios = baseDatos.obtenerSocios()
        adaptadorSocios.actualizarLista(nuevosSocios)
    }
}
