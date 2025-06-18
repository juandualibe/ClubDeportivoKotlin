package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ActividadesListActivity
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.Actividad

class AddEditActividadActivity : AppCompatActivity() {
    private var actividadId: Int? = null  // null si es nueva

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit_actividad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias UI
        val editNombre = findViewById<EditText>(R.id.editNombre)
        val editDescripcion = findViewById<EditText>(R.id.editDescripcion)
        val editHorario = findViewById<EditText>(R.id.editHorario)
        val editMonto = findViewById<EditText>(R.id.editMonto)
        val editCupo = findViewById<EditText>(R.id.editCupo)
        val spinnerDia = findViewById<Spinner>(R.id.spinnerDia)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Configurar Spinner
        val diasSemana = arrayOf("Día de la semana", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diasSemana)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDia.adapter = adapter

        // Ver si estamos editando una actividad existente
        actividadId = intent.getIntExtra("ACTIVIDAD_ID", -1).takeIf { it != -1 }
        if (actividadId != null) {
            val bd = ClubDeportivoBD(this)
            val actividad = bd.obtenerActividadPorId(actividadId!!)
            bd.close()
            if (actividad != null) {
                editNombre.setText(actividad.nombre)
                editDescripcion.setText(actividad.descripcion)
                editHorario.setText(actividad.horario)
                editMonto.setText(actividad.monto.toString())
                editCupo.setText(actividad.cupoMaximo.toString())
                val posicionDia = diasSemana.indexOf(actividad.dia)
                if (posicionDia != -1) spinnerDia.setSelection(posicionDia)
            }
        }

        // Volver
        btnVolver.setOnClickListener {
            startActivity(Intent(this, ActividadesListActivity::class.java))
            finish()
        }

        // Confirmar alta o edición
        btnConfirmar.setOnClickListener {
            val nombre = editNombre.text.toString()
            val descripcion = editDescripcion.text.toString()
            val horario = editHorario.text.toString()
            val monto = editMonto.text.toString().toDoubleOrNull() ?: 0.0
            val cupoMaximo = editCupo.text.toString().toIntOrNull() ?: 0
            val dia = spinnerDia.selectedItem.toString()

            if (nombre.isEmpty() || descripcion.isEmpty() || horario.isEmpty() || monto <= 0 || cupoMaximo <= 0 || dia == "Día de la semana") {
                Toast.makeText(this, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bd = ClubDeportivoBD(this)
            if (actividadId == null) {
                // Crear nueva actividad
                bd.insertarActividad(
                    Actividad(
                        id = 0, // La BD debería asignarlo automáticamente
                        nombre = nombre,
                        descripcion = descripcion,
                        dia = dia,
                        horario = horario,
                        monto = monto,
                        cupoMaximo = cupoMaximo
                    )
                )
            } else {
                // Actualizar actividad existente
                bd.actualizarActividad(
                    Actividad(
                        id = actividadId!!,
                        nombre = nombre,
                        descripcion = descripcion,
                        dia = dia,
                        horario = horario,
                        monto = monto,
                        cupoMaximo = cupoMaximo
                    )
                )
            }
            bd.close()

            // Mostrar pantalla de éxito
            startActivity(Intent(this, AddedEditedActivity::class.java))
            finish()
        }
    }
}