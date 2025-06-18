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

/**
 * Pantalla para inscribir a un socio en actividades.
 */
class RegisterInActivityActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView // Lista de actividades
    private lateinit var adaptador: AdaptadorRegisterActividades // Adaptador para la lista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.activity_register_in_activity) // Carga el diseño

        // Ajusta márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sacamos ID y nombre del socio
        val socioNumero = intent.getIntExtra("socio_numero", -1)
        val socioNombre = intent.getStringExtra("socio_nombre") ?: "Desconocido"

        val db = ClubDeportivoBD(this) // Conexión a la base de datos

        // Agarramos el RecyclerView y el botón de volver
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        recyclerView = findViewById(R.id.recyclerViewActividades)

        // Configuramos la lista de actividades
        recyclerView.layoutManager = LinearLayoutManager(this) // Lista vertical
        val actividades = db.obtenerActividades().toMutableList() // Sacamos actividades
        recyclerView.contentDescription = getString(R.string.activity_list_description) // Accesibilidad
        adaptador = AdaptadorRegisterActividades(actividades, socioNumero, socioNombre) // Adaptador
        recyclerView.adapter = adaptador

        // Botón para volver a los detalles del socio
        btnVolver.setOnClickListener {
            val intent = Intent(this, SocioDetailsActivity::class.java).apply {
                putExtra("SOCIO_NOMBRE", socioNombre)
                putExtra("SOCIO_NUMERO", socioNumero)
            }
            startActivity(intent)
            finish() // Cerramos esta pantalla
        }
    }
}