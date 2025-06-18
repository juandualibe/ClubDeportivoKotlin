package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.NoSocio

/**
 * Pantalla para agregar o editar un no socio.
 */
class AddEditNoSocioActivity : AppCompatActivity() {
    private lateinit var bd: ClubDeportivoBD // Base de datos
    private var noSocioId: Int? = null // ID del no socio (si es edición)
    private lateinit var etNombre: EditText // Campos de texto
    private lateinit var etApellido: EditText
    private lateinit var etDni: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etPagoDiario: EditText
    private lateinit var cbAptoFisico: CheckBox // Checkbox
    private lateinit var btnConfirmar: Button // Botones
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_no_socio) // Carga el diseño

        bd = ClubDeportivoBD(this) // Conexión a la base de datos

        // Agarramos los elementos del diseño
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etDni = findViewById(R.id.etDNI)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        etPagoDiario = findViewById(R.id.etPagoDiario)
        cbAptoFisico = findViewById(R.id.cbAptoFisico)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnVolver = findViewById(R.id.btnVolver)

        // Si es edición, cargamos los datos del no socio
        noSocioId = intent.getIntExtra("NO_SOCIO_ID", -1).takeIf { it != -1 }
        noSocioId?.let { id ->
            val noSocio = bd.obtenerNoSocios().find { it.id == id }
            noSocio?.let {
                etNombre.setText(it.nombre)
                etApellido.setText(it.apellido)
                etDni.setText(it.dni)
                etCorreo.setText(it.correo)
                etTelefono.setText(it.telefono)
                etPagoDiario.setText(it.pagoDiario.toString())
                cbAptoFisico.isChecked = it.aptoFisico
            }
        }

        // Botón para guardar el no socio
        btnConfirmar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val pagoDiario = etPagoDiario.text.toString().toDoubleOrNull() ?: 0.0
            val aptoFisico = cbAptoFisico.isChecked

            // Validamos campos obligatorios
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Por favor, completa los campos obligatorios (Nombre, Apellido, DNI)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chequeamos DNI duplicado
            if (bd.dniYaExiste(dni, noSocioId)) {
                Toast.makeText(this, "Ya existe un socio o no socio con ese DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto NoSocio
            val noSocio = NoSocio(
                id = noSocioId ?: 0, nombre = nombre, apellido = apellido, dni = dni,
                correo = if (correo.isEmpty()) null else correo, telefono = telefono,
                pagoDiario = pagoDiario, aptoFisico = aptoFisico
            )

            // Guardamos o actualizamos el no socio
            val exito = if (noSocioId == null) bd.insertarNoSocio(noSocio) else bd.actualizarNoSocio(noSocio)
            if (exito) {
                val intent = Intent(this, AddedEditedNoSocioActivity::class.java)
                startActivity(intent)
                finish() // Cierra esta pantalla
            } else {
                Toast.makeText(this, "Error al guardar no socio", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para volver a la lista de no socios
        btnVolver.setOnClickListener {
            startActivity(Intent(this, NoSocioListActivity::class.java))
            finish()
        }
    }
}