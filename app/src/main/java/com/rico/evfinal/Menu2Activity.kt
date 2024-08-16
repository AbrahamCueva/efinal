package com.rico.evfinal

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Menu2Activity : AppCompatActivity() {

    private lateinit var imgUserProfile: ImageView
    private lateinit var txtNombre: TextView
    private lateinit var txtNombre1: TextView
    private lateinit var txtCelular: TextView
    private lateinit var txtDNI: TextView
    private lateinit var txtUsername: TextView
    private lateinit var txtCodigo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtener referencias de los TextView y ImageView
        imgUserProfile = findViewById(R.id.imageView5)
        txtNombre = findViewById(R.id.textViewNombreUsuario)
        txtNombre1 = findViewById(R.id.textView8)
        txtCelular = findViewById(R.id.textView11)
        txtDNI = findViewById(R.id.textView9)
        txtUsername = findViewById(R.id.textView12)
        txtCodigo = findViewById(R.id.txtCodigo)

        // Obtener el username del Intent
        val username = intent.getStringExtra("username")

        if (username == null) {
            // Podrías mostrar un mensaje de error o cerrar la actividad
            txtNombre.text = "Error: username no proporcionado"
            return
        }

        // Buscar los datos del usuario en la base de datos
        val admin = AdminSQLiteOpenHelper(this)
        val db: SQLiteDatabase = admin.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id_user, nombre, celular, dni, imagen FROM usuariologin WHERE username = ?",
            arrayOf(username)
        )

        if (cursor.moveToFirst()) {
            // Mostrar los datos en los TextView
            txtCodigo.text = "Codigo: ${cursor.getString(0)}"
            txtNombre.text = cursor.getString(1) // nombre
            txtNombre1.text = "Nombre: ${cursor.getString(1)}"
            txtCelular.text = "Celular: ${cursor.getString(2)}" // celular
            txtDNI.text = "DNI: ${cursor.getString(3)}" // dni
            txtUsername.text = "Usuario: $username" // username

            // Cargar y mostrar la imagen si existe
            val imageBlob = cursor.getBlob(4)
            if (imageBlob != null) {
                val bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.size)
                imgUserProfile.setImageBitmap(bitmap)
            } else {
                imgUserProfile.setImageResource(R.drawable.empleado) // Imagen por defecto
            }
        } else {
            // Manejar el caso en que no se encuentra el usuario
            txtNombre.text = "Usuario no encontrado"
        }

        cursor.close()
        db.close()
    }

    override fun onCreateOptionsMenu(menu2: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu2, menu2)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.productos -> {
                val intent = Intent(this, ProductosActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.ventas -> {
                val intent = Intent(this, VentasActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.exit -> {
                finishAffinity()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
