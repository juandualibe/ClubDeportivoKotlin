package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD
import com.example.clubdeportivog3.model.Socio

class AddEditSocioActivity : AppCompatActivity() {

    private lateinit var bd: ClubDeportivoBD
    private var socioId: Int? = null

    private lateinit var editNombre: EditText
    private lateinit var editApellido: EditText
    private lateinit var editDni: EditText
    private lateinit var editCorreo: EditText
    private lateinit var editTelefono: EditText
    private lateinit var editCuota: EditText
    private lateinit var checkAptoFisico: CheckBox
    private lateinit var checkCarnetEntregado: CheckBox
    private lateinit var checkPagoAlDia: CheckBox
    private lateinit var btnConfirmar: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_socio)

        bd = ClubDeportivoBD(this)

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

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificación DNI duplicado
            val dniDuplicado = bd.dniYaExiste(dni, socioId)

            if (dniDuplicado) {
                Toast.makeText(this, "Ya existe un socio con ese DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val socio = Socio(
                id = socioId ?: 0,
                nombre = nombre,
                apellido = apellido,
                dni = dni,
                correo = if (correo.isEmpty()) null else correo,
                telefono = telefono,
                cuota = cuota,
                aptoFisico = aptoFisico,
                carnetEntregado = carnetEntregado,
                pagoAlDia = pagoAlDia
            )

            val exito = if (socioId == null) {
                bd.insertarSocio(socio)
            } else {
                bd.actualizarSocio(socio)
            }

            if (exito) {
                val intent = Intent(this, AddedEditedSocioActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar socio", Toast.LENGTH_SHORT).show()
            }
        }


        btnVolver.setOnClickListener {
            startActivity(Intent(this, SocioListActivity::class.java))
            finish()
        }
    }
}