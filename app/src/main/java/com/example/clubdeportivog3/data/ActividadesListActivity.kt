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

class ActividadesListActivity : AppCompatActivity() {
    private lateinit var adaptador: AdaptadorActividades
    private val listaActividades = mutableListOf<Actividad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_activity_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cargarActividades()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewActividades)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adaptador = AdaptadorActividades(listaActividades) { accion, actividad ->
            when (accion) {
                "editar" -> {
                    val intent = Intent(this, AddEditActividadActivity::class.java)
                    intent.putExtra("ACTIVIDAD_ID", actividad.id)
                    startActivity(intent)
                }
                "eliminar" -> {
                    AlertDialog.Builder(this)
                        .setTitle("Eliminar Actividad")
                        .setMessage("¿Seguro que quieres eliminar la actividad \"${actividad.nombre}\"?")
                        .setPositiveButton("Sí") { _, _ ->
                            val bd = ClubDeportivoBD(this)
                            val exito = bd.eliminarActividad(actividad.id)
                            bd.close()

                            if (exito) {
                                listaActividades.remove(actividad)
                                adaptador.notifyDataSetChanged()
                                // En vez de Toast
                                val intent = Intent(this, DeletedActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Toast para error
                                Toast.makeText(this, "Error al eliminar la actividad", Toast.LENGTH_SHORT).show()
                            }

                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        }
        recyclerView.adapter = adaptador

        findViewById<Button>(R.id.btnAgregar).setOnClickListener {
            val intent = Intent(this, AddEditActividadActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarActividades()
        adaptador.notifyDataSetChanged()
    }

    private fun cargarActividades() {
        val bd = ClubDeportivoBD(this)
        listaActividades.clear()
        listaActividades.addAll(bd.obtenerActividades())
        bd.close()
    }
}