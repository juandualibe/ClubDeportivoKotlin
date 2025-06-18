package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.Socio

/**
 * Pantalla para agregar o editar un socio.
 */
class AddEditSocioActivity : AppCompatActivity() {
    private lateinit var bd: ClubDeportivoBD // Base de datos
    private var socioId: Int? = null // ID del socio (si es edición)
    private lateinit var editNombre: EditText // Campos de texto
    private lateinit var editApellido: EditText
    private lateinit var editDni: EditText
    private lateinit var editCorreo: EditText
    private lateinit var editTelefono: EditText
    private lateinit var editCuota: EditText
    private lateinit var checkAptoFisico: CheckBox // Checkboxes
    private lateinit var checkCarnetEntregado: CheckBox
    private lateinit var checkPagoAlDia: CheckBox
    private lateinit var btnConfirmar: Button // Botones
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_socio) // Carga el diseño

        bd = ClubDeportivoBD(this) // Conexión a la base de datos

        // Agarramos los elementos del diseño
        editNombre = findViewById(R.id.editNombre)
        editApellido = findViewById(R.id.editApellido)
        editDni = findViewById(R.id.editDni)
        editCorreo = findViewById(R.id.editCorreo)
        editTelefono = findViewById(R.id.editTelefono)
        editCuota = findViewById(R.id.editCuota)
        checkAptoFisico = findViewById(R.id.checkAptoFisico)
        checkCarnetEntregado = findViewById(R.id.checkCarnetEntregado)
        checkPagoAlDia = findViewById(R.id.checkPagoAlDia)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnVolver = findViewById(R.id.btnVolver)

        // Si es edición, cargamos los datos del socio
        socioId = intent.getIntExtra("SOCIO_ID", -1).takeIf { it != -1 }
        socioId?.let { id ->
            val socio = bd.obtenerSocios().find { it.id == id }
            socio?.let {
                editNombre.setText(it.nombre)
                editApellido.setText(it.apellido)
                editDni.setText(it.dni)
                editCorreo.setText(it.correo)
                editTelefono.setText(it.telefono)
                editCuota.setText(it.cuota?.toString() ?: "")
                checkAptoFisico.isChecked = it.aptoFisico
                checkCarnetEntregado.isChecked = it.carnetEntregado
                checkPagoAlDia.isChecked = it.pagoAlDia
            }
        }

        // Botón para guardar el socio
        btnConfirmar.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val dni = editDni.text.toString().trim()
            val correo = editCorreo.text.toString().trim()
            val telefono = editTelefono.text.toString().trim()
            val cuota = editCuota.text.toString().toDoubleOrNull() ?: 0.0
            val aptoFisico = checkAptoFisico.isChecked
            val carnetEntregado = checkCarnetEntregado.isChecked
            val pagoAlDia = checkPagoAlDia.isChecked

            // Cuota fija para todos los socios
            val CUOTA_FIJA = 15000.0
            val deuda = CUOTA_FIJA - cuota

            // Mostramos si hay deuda o exceso
            if (deuda > 0) {
                Toast.makeText(this, "⚠️ El socio adeuda $${deuda}. Cuota correcta: $${CUOTA_FIJA}", Toast.LENGTH_LONG).show()
            } else if (deuda < 0) {
                Toast.makeText(this, "ℹ️ El socio pagó $${Math.abs(deuda)} de más. Cuota correcta: $${CUOTA_FIJA}", Toast.LENGTH_LONG).show()
            }

            // Validamos campos obligatorios
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chequeamos DNI duplicado
            if (bd.dniYaExiste(dni, socioId)) {
                Toast.makeText(this, "Ya existe un socio con ese DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto Socio
            val socio = Socio(
                id = socioId ?: 0, nombre = nombre, apellido = apellido, dni = dni,
                correo = if (correo.isEmpty()) null else correo, telefono = telefono,
                cuota = cuota, aptoFisico = aptoFisico, carnetEntregado = carnetEntregado,
                pagoAlDia = pagoAlDia
            )

            // Guardamos o actualizamos el socio
            val exito = if (socioId == null) bd.insertarSocio(socio) else bd.actualizarSocio(socio)
            if (exito) {
                val intent = Intent(this, AddedEditedSocioActivity::class.java)
                startActivity(intent)
                finish() // Cierra esta pantalla
            } else {
                Toast.makeText(this, "Error al guardar socio", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para volver a la lista de socios
        btnVolver.setOnClickListener {
            startActivity(Intent(this, SocioListActivity::class.java))
            finish()
        }
    }
}