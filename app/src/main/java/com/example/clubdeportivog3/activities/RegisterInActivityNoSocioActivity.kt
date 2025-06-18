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
import com.example.clubdeportivog3.data.AdaptadorRegisterActividadesNoSocio
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD

/**
 * Pantalla para inscribir a un no socio en actividades.
 */
class RegisterInActivityNoSocioActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView // Lista para mostrar actividades
    private lateinit var adaptador: AdaptadorRegisterActividadesNoSocio // Adaptador para la lista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.activity_register_in_activity_no_socio) // Carga el diseño

        // Ajusta márgenes para no tapar barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sacamos el ID y nombre del no socio del Intent
        val noSocioNumero = intent.getIntExtra("no_socio_numero", -1)
        val noSocioNombre = intent.getStringExtra("no_socio_nombre") ?: "Desconocido"

        val db = ClubDeportivoBD(this) // Conectamos con la base de datos

        // Agarramos las actividades de la base de datos
        val actividades = db.obtenerActividades().toMutableList()

        // Configuramos la lista (RecyclerView)
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        recyclerView = findViewById(R.id.recyclerViewActividades)
        recyclerView.layoutManager = LinearLayoutManager(this) // Lista vertical
        adaptador = AdaptadorRegisterActividadesNoSocio(actividades, noSocioNumero, noSocioNombre) // Adaptador para mostrar actividades
        recyclerView.adapter = adaptador
        recyclerView.contentDescription = getString(R.string.activity_list_description) // Descripción para accesibilidad

        // Botón para volver a los detalles del no socio
        btnVolver.setOnClickListener {
            val intent = Intent(this, NoSocioDetailsActivity::class.java).apply {
                putExtra("NO_SOCIO_NOMBRE", noSocioNombre)
                putExtra("NO_SOCIO_ID", noSocioNumero)
            }
            startActivity(intent)
            finish() // Cerramos esta pantalla
        }
    }
}