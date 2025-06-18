package com.example.clubdeportivog3.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivog3.R
import com.example.clubdeportivog3.data.ClubDeportivoBD

/**
 * Pantalla de login donde el usuario mete su email y contraseña para entrar al sistema.
 */

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Carga el layout del login

        // Agarramos los elementos del layout
        val email = findViewById<EditText>(R.id.editTextEmail)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val button = findViewById<Button>(R.id.buttonIngresar)
        val registerText = findViewById<TextView>(R.id.textViewRegistrarse)
        val checkBoxMostrarPassword = findViewById<CheckBox>(R.id.checkBoxMostrarPassword)

        val clubDeportivoBD = ClubDeportivoBD(this)

        // Listener para mostrar/ocultar contraseña con el CheckBox
        checkBoxMostrarPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Mostrar contraseña
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Ocultar contraseña
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Mantener cursor al final del texto
            password.setSelection(password.text.length)
        }

        // Botón ingresar
        button.setOnClickListener {
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString()

            if (userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
                val isValidUser = clubDeportivoBD.verificarLogin(userEmail, userPassword)
                if (isValidUser) {
                    Toast.makeText(this, "Bienvenido, $userEmail", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuario no registrado o contraseña incorrecta. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Texto registrarse
        registerText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}