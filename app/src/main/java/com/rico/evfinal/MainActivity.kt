package com.rico.evfinal

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val continueButton: Button = findViewById(R.id.continueButton)
        val exitButton: Button = findViewById(R.id.exitButton)

        continueButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        exitButton.setOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Seguro que quieres salir?")
            .setPositiveButton("Sí") { dialog, id ->
                finish() // Cierra la aplicación
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss() // Cierra el diálogo
            }

        // Personaliza los botones y el mensaje
        val dialog: AlertDialog = builder.create()

        // Cambia el color de los botones
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.holo_red_dark))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }

        dialog.show()
    }
}
