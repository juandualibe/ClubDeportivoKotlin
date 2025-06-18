package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.AdaptadorNoSocios
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.NoSocio

/**
 * Pantalla que muestra la lista de no socios y permite agregar, editar o eliminar.
 */
class NoSocioListActivity : AppCompatActivity() {
    private lateinit var recyclerViewNoSocios: RecyclerView // Lista de no socios
    private lateinit var btnAgregar: Button // Botón para agregar
    private lateinit var btnVolver: Button // Botón para volver
    private lateinit var baseDatos: ClubDeportivoBD // Base de datos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.activity_no_socio_list) // Carga el diseño

        // Ajusta márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Agarramos los elementos del diseño
        recyclerViewNoSocios = findViewById(R.id.recyclerViewNoSocios)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnVolver = findViewById(R.id.btnVolver)
        baseDatos = ClubDeportivoBD(this) // Conexión a la base de datos

        // Configuramos la lista
        recyclerViewNoSocios.layoutManager = LinearLayoutManager(this) // Lista vertical
        val listaNoSocios: List<NoSocio> = baseDatos.obtenerNoSocios() // Sacamos no socios

        // Adaptador para mostrar no socios
        val adaptadorNoSocios = AdaptadorNoSocios(listaNoSocios) { accion, noSocio ->
            when (accion) {
                "editar" -> {
                    // Vamos a editar el no socio
                    val intent = Intent(this, AddEditNoSocioActivity::class.java)
                    intent.putExtra("NO_SOCIO_ID", noSocio.id)
                    startActivity(intent)
                }
                "eliminar" -> {
                    // Eliminamos el no socio
                    val eliminado = baseDatos.eliminarNoSocio(noSocio.id)
                    if (eliminado) {
                        // Mostramos confirmación
                        val intent = Intent(this, DeletedNoSocioActivity::class.java)
                        startActivity(intent)
                        finish() // Cerramos esta pantalla
                    } else {
                        // Error al eliminar
                        Toast.makeText(this, "No se pudo eliminar el NoSocio", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        recyclerViewNoSocios.adapter = adaptadorNoSocios

        // Botón para agregar un no socio
        btnAgregar.setOnClickListener {
            val intent = Intent(this, AddEditNoSocioActivity::class.java)
            startActivity(intent)
        }

        // Botón para volver al menú
        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}