package com.rico.evfinal

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream


class RegisterActivity : AppCompatActivity() {

    private lateinit var edtNombre: EditText
    private lateinit var edtCelular: EditText
    private lateinit var edtDni: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnRegister: Button
    private var selectedImage: Bitmap? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var dbHelper: AdminSQLiteOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inicializar los elementos
        edtNombre = findViewById(R.id.edtNombre)
        edtCelular = findViewById(R.id.edtCelular)
        edtDni = findViewById(R.id.edtDni)
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnRegister = findViewById(R.id.btnRegister)
        dbHelper = AdminSQLiteOpenHelper(this)

        // Configurar el botón para seleccionar imagen
        btnSelectImage.setOnClickListener {
            openImageChooser()
        }

        // Configurar el botón para registrar
        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    // Método para abrir la galería y seleccionar una imagen
    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Manejar el resultado de la selección de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            selectedImage = BitmapFactory.decodeStream(inputStream)
        }
    }

    // Método para registrar el usuario en la base de datos
    private fun registerUser() {
        val nombre = edtNombre.text.toString().trim()
        val celular = edtCelular.text.toString().trim()
        val dni = edtDni.text.toString().trim()
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (nombre.isEmpty() || celular.isEmpty() || dni.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("celular", celular)
            put("dni", dni)
            put("username", username)
            put("clave_user", password)
            if (selectedImage != null) {
                val baos = ByteArrayOutputStream()
                selectedImage?.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val imageInBytes = baos.toByteArray()
                put("imagen", imageInBytes)
            }
        }

        val newRowId = db.insert("usuariologin", null, values)
        db.close()

        if (newRowId != -1L) {
            Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
            goToLogin(null)
        } else {
            Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para redirigir al usuario a la pantalla de inicio de sesión
    fun goToLogin(view: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
}
