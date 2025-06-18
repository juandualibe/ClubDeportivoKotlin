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

class RegisterInActivityNoSocioActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorRegisterActividadesNoSocio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_in_activity_no_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener no_socio_numero y no_socio_nombre del Intent
        val noSocioNumero = intent.getIntExtra("no_socio_numero", -1)
        val noSocioNombre = intent.getStringExtra("no_socio_nombre") ?: "Desconocido"

        val db = ClubDeportivoBD(this)

        val actividades = db.obtenerActividades()

        // Referencias a los elementos de la UI
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        recyclerView = findViewById(R.id.recyclerViewActividades)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorRegisterActividadesNoSocio(actividades, noSocioNumero, noSocioNombre)
        recyclerView.adapter = adaptador
        recyclerView.contentDescription = getString(R.string.activity_list_description)

        // Botón Volver
        btnVolver.setOnClickListener {
            val intent = Intent(this, NoSocioDetailsActivity::class.java).apply {
                putExtra("NO_SOCIO_NOMBRE", noSocioNombre)
                putExtra("NO_SOCIO_ID", noSocioNumero)
            }
            startActivity(intent)
            finish()
        }
    }
}