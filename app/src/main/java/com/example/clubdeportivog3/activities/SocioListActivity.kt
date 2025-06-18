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

/**
 * Pantalla que muestra la lista de socios y permite agregar, editar o eliminar.
 */

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

        // Agarramos los elementos del layout
        recyclerView = findViewById(R.id.recyclerViewSocios)
        btnAgregarSocio = findViewById(R.id.btnAgregar)
        btnVolver = findViewById(R.id.btnVolver)
        baseDatos = ClubDeportivoBD(this)

        // INICIO: AGREGADO - Inicialización del botón Vencimientos hoy
        val btnVencimientos = findViewById<Button>(R.id.btnVencimientos)
        btnVencimientos.setOnClickListener {
            val intent = Intent(this, ExpirationActivity::class.java)
            startActivity(intent)
        }
        // FIN: AGREGADO

        origen = intent.getStringExtra("ORIGEN") ?: "MenuActivity"

        // Configuramos el RecyclerView para mostrar los socios
        recyclerView.layoutManager = LinearLayoutManager(this)
        adaptadorSocios = AdaptadorSocios(emptyList()) { accion: String, socio: Socio ->
            when (accion) {
                // Vamos a la pantalla para editar el socio
                "editar" -> {
                    val intent = Intent(this, AddEditSocioActivity::class.java).apply {
                        putExtra("SOCIO_ID", socio.id)
                    }
                    startActivity(intent)
                }

                // Eliminamos el socio de la base de datos
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

        // Botón para agregar un nuevo socio
        btnAgregarSocio.setOnClickListener {
            val intent = Intent(this, AddEditSocioActivity::class.java)
            startActivity(intent)
        }

        // Botón para volver al menú o a la pantalla de vencimientos
        btnVolver.setOnClickListener {
            val intent = Intent(
                this,
                if (origen == "ExpirationActivity") ExpirationActivity::class.java else MenuActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    // Cuando volvemos a esta pantalla, actualizamos la lista
    override fun onResume() {
        super.onResume()
        actualizarListaSocios()
    }

    // Función para recargar la lista de socios desde la base de datos
    private fun actualizarListaSocios() {
        val nuevosSocios = baseDatos.obtenerSocios()
        adaptadorSocios.actualizarLista(nuevosSocios)
    }
}