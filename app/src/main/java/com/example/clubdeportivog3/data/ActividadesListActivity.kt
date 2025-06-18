package com.example.clubdeportivog3.data

import android.app.AlertDialog
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
import com.example.clubdeportivog3.activities.AddEditActividadActivity
import com.example.clubdeportivog3.activities.DeletedActivity
import com.example.clubdeportivog3.activities.MenuActivity
import com.example.clubdeportivog3.model.Actividad

/**
 * Pantalla que muestra la lista de actividades y permite agregar, editar o eliminar.
 */
class ActividadesListActivity : AppCompatActivity() {
    private lateinit var adaptador: AdaptadorActividades // Adaptador para la lista
    private val listaActividades = mutableListOf<Actividad>() // Lista de actividades

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Usa toda la pantalla
        setContentView(R.layout.activity_activity_list) // Carga el diseño

        // Ajusta márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cargarActividades() // Carga las actividades

        // Configura la lista (RecyclerView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewActividades)
        recyclerView.layoutManager = LinearLayoutManager(this) // Lista vertical

        // Adaptador para manejar acciones de editar o eliminar
        adaptador = AdaptadorActividades(listaActividades) { accion, actividad ->
            when (accion) {
                "editar" -> {
                    // Va a la pantalla de edición
                    val intent = Intent(this, AddEditActividadActivity::class.java)
                    intent.putExtra("ACTIVIDAD_ID", actividad.id)
                    startActivity(intent)
                }
                "eliminar" -> {
                    // Confirma antes de eliminar
                    AlertDialog.Builder(this)
                        .setTitle("Eliminar Actividad")
                        .setMessage("¿Seguro que quieres eliminar la actividad \"${actividad.nombre}\"?")
                        .setPositiveButton("Sí") { _, _ ->
                            val bd = ClubDeportivoBD(this)
                            val exito = bd.eliminarActividad(actividad.id)
                            bd.close()
                            if (exito) {
                                listaActividades.remove(actividad)
                                adaptador.notifyDataSetChanged() // Actualiza la lista
                                val intent = Intent(this, DeletedActivity::class.java)
                                startActivity(intent)
                                finish() // Cierra la pantalla
                            } else {
                                Toast.makeText(this, "Error al eliminar la actividad", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        }
        recyclerView.adapter = adaptador

        // Botón para agregar una actividad
        findViewById<Button>(R.id.btnAgregar).setOnClickListener {
            val intent = Intent(this, AddEditActividadActivity::class.java)
            startActivity(intent)
        }

        // Botón para volver al menú
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Actualiza la lista al volver a la pantalla
    override fun onResume() {
        super.onResume()
        cargarActividades()
        if (::adaptador.isInitialized) {
            adaptador.notifyDataSetChanged() // Refresca la lista
        }
    }

    // Carga las actividades desde la base de datos
    private fun cargarActividades() {
        val bd = ClubDeportivoBD(this)
        listaActividades.clear()
        listaActividades.addAll(bd.obtenerActividades())
        bd.close()
    }
}