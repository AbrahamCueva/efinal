package com.rico.evfinal

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.graphics.Paint
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var dbHelper: AdminSQLiteOpenHelper
    private lateinit var tvCreateAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inicializar los componentes
        edtUsuario = findViewById(R.id.edtUsuario)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)
        dbHelper = AdminSQLiteOpenHelper(this)

        // Configurar el botón de inicio de sesión
        btnLogin.setOnClickListener {
            val username = edtUsuario.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (validateLogin(username, password)) {
                val intent = Intent(this, Menu2Activity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            } else {
                // Mostrar un mensaje de error
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        // Subrayar el texto de "Crear una"
        tvCreateAccount.paintFlags = tvCreateAccount.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Redirigir a la actividad de registro
        tvCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM usuariologin WHERE username = ? AND clave_user = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        return isValid
    }
}
