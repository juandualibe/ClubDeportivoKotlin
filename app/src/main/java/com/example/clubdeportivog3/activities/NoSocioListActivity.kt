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

class NoSocioListActivity : AppCompatActivity() {

    private lateinit var recyclerViewNoSocios: RecyclerView
    private lateinit var btnAgregar: Button
    private lateinit var btnVolver: Button
    private lateinit var baseDatos: ClubDeportivoBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_no_socio_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewNoSocios = findViewById(R.id.recyclerViewNoSocios)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnVolver = findViewById(R.id.btnVolver)
        baseDatos = ClubDeportivoBD(this)

        recyclerViewNoSocios.layoutManager = LinearLayoutManager(this)

        // Obtener no socios desde la base de datos
        val listaNoSocios: List<NoSocio> = baseDatos.obtenerNoSocios()

        val adaptadorNoSocios = AdaptadorNoSocios(listaNoSocios) { accion, noSocio ->
            when (accion) {
                "editar" -> {
                    val intent = Intent(this, AddEditNoSocioActivity::class.java)
                    intent.putExtra("NO_SOCIO_ID", noSocio.id)
                    startActivity(intent)
                }
                "eliminar" -> {
                    val eliminado = baseDatos.eliminarNoSocio(noSocio.id)
                    if (eliminado) {
                        // Navegar a la pantalla DeletedNoSocioActivity
                        val intent = Intent(this, DeletedNoSocioActivity::class.java)
                        startActivity(intent)
                        finish() // Opcional, si querés cerrar esta activity
                    } else {
                        Toast.makeText(this, "No se pudo eliminar el NoSocio", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        recyclerViewNoSocios.adapter = adaptadorNoSocios

        btnAgregar.setOnClickListener {
            val intent = Intent(this, AddEditNoSocioActivity::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}