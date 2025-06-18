package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Referencias a los elementos del layout
        val email = findViewById<EditText>(R.id.editTextEmailRegister)
        val password = findViewById<EditText>(R.id.editTextPasswordRegister)
        val confirm = findViewById<EditText>(R.id.editTextConfirmRegister)
        val buttonRegistrar = findViewById<Button>(R.id.buttonRegistrar)
        val textViewIngresar = findViewById<TextView>(R.id.textViewIngresar)

        val bd = ClubDeportivoBD(this)

        // Acción al hacer clic en el botón "Registrar"
        buttonRegistrar.setOnClickListener {
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()
            val userConfirm = confirm.text.toString()

            if (userEmail.isNotEmpty() && userPassword.isNotEmpty() && userConfirm.isNotEmpty()) {
                // Validación de formato de email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(this, "El email no es válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (userPassword == userConfirm) {
                    // Verificar si el email ya está registrado
                    val exito = bd.registrarAdmin(userEmail, userPassword)
                    if (exito) {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Ese email ya está registrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción al hacer clic en el texto "¿Ya tienes una cuenta? Ingresar"
        textViewIngresar.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}