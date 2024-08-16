package com.rico.evfinal

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import kotlinx.android.synthetic.main.activity_ventas.*
import java.util.*

class VentasActivity : AppCompatActivity() {

    private lateinit var etClienteNombre: EditText
    private lateinit var etFecha: EditText
    private lateinit var spCajeros: Spinner
    private lateinit var spSeleccionarProducto: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var tvTotalProducto: TextView
    private lateinit var btnNuevo: Button
    private lateinit var btnCalcularTotal: Button
    private lateinit var btnRegistrar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tbVentas: TableLayout

    private lateinit var admin: AdminSQLiteOpenHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var preciosList: MutableList<Double>
    private lateinit var cajerosList: MutableList<String>
    private lateinit var productosList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etFecha = findViewById(R.id.etFecha)
        etClienteNombre = findViewById(R.id.etClienteNombre)
        spCajeros = findViewById(R.id.spCajeros)
        spSeleccionarProducto = findViewById(R.id.spSeleccionarProducto)
        etCantidad = findViewById(R.id.etCantidad)
        tvTotalProducto = findViewById(R.id.tvTotalProducto)
        btnNuevo = findViewById(R.id.btnNuevaVenta)
        btnCalcularTotal = findViewById(R.id.btnCalcularTotal)
        btnRegistrar = findViewById(R.id.btnRegistrarVenta)
        btnActualizar = findViewById(R.id.btnActualizarVenta)
        btnBuscar = findViewById(R.id.btnBuscarVenta)
        btnEliminar = findViewById(R.id.btnEliminarVenta)
        tbVentas = findViewById(R.id.tbVentas)

        admin = AdminSQLiteOpenHelper(this)
        db = admin.readableDatabase

        cargarMozos()
        cargarProductos()

        etFecha.setOnClickListener {
            showDatePicker()
        }

        btnNuevo.setOnClickListener { limpiarCampos() }
        btnCalcularTotal.setOnClickListener { calcularTotalProducto() }
        btnRegistrar.setOnClickListener { registrarVenta() }
        btnActualizar.setOnClickListener { actualizarVenta() }
        btnBuscar.setOnClickListener { buscarVenta() }
        btnEliminar.setOnClickListener { eliminarVenta() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            etFecha.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun cargarMozos() {
        val mozoCursor: Cursor = db.rawQuery("SELECT nombre FROM usuariologin", null)
        cajerosList = mutableListOf()
        while (mozoCursor.moveToNext()) {
            cajerosList.add(mozoCursor.getString(0))
        }
        mozoCursor.close()
        spCajeros.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cajerosList)
    }

    private fun cargarProductos() {
        val productoCursor: Cursor = db.rawQuery("SELECT producto, precio FROM productos", null)
        productosList = mutableListOf()
        preciosList = mutableListOf()
        while (productoCursor.moveToNext()) {
            productosList.add("${productoCursor.getString(0)} (S/${productoCursor.getDouble(1)})")
            preciosList.add(productoCursor.getDouble(1))
        }
        productoCursor.close()
        spSeleccionarProducto.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productosList)
    }

    private fun calcularTotalProducto() {
        val cantidad = etCantidad.text.toString().toIntOrNull() ?: return
        val precio = preciosList[spSeleccionarProducto.selectedItemPosition]
        val total = precio * cantidad
        tvTotalProducto.text = "Total: S/$total"
    }

    private fun registrarVenta() {
        val fecha = etFecha.text.toString()
        val cajero = spCajeros.selectedItem.toString()
        val cantidad = etCantidad.text.toString().toIntOrNull() ?: return
        val precio = preciosList[spSeleccionarProducto.selectedItemPosition]
        val total = precio * cantidad
        val cliente = etClienteNombre.text.toString()

        val idProductoCursor = db.rawQuery(
            "SELECT id_producto FROM productos WHERE producto = ?",
            arrayOf(spSeleccionarProducto.selectedItem.toString().split(" ")[0])
        )

        if (idProductoCursor.moveToFirst()) {
            val idProducto = idProductoCursor.getInt(idProductoCursor.getColumnIndex("id_producto"))

            val valores = ContentValues().apply {
                put("fecha", fecha)
                put("vendedor", cajero)
                put("cantidad", cantidad)
                put("total", total)
                put("cliente", cliente)
                put("id_producto", idProducto)
            }

            val newRowId = db.insert("ventas", null, valores)
            if (newRowId == -1L) {
                Toast.makeText(this, "Error al registrar la venta", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Venta registrada con éxito", Toast.LENGTH_SHORT).show()
                limpiarCampos()
                mostrarVentas()  // Permanecer en la misma actividad
            }
        } else {
            Toast.makeText(this, "Error al obtener el ID del producto", Toast.LENGTH_SHORT).show()
        }

        idProductoCursor.close()
    }

    private fun actualizarVenta() {
        val idVenta = etCantidad.text.toString().toIntOrNull() ?: return
        val nuevaFecha = etFecha.text.toString()
        val nuevoCajero = spCajeros.selectedItem.toString()
        val nuevaCantidad = etCantidad.text.toString().toIntOrNull() ?: return
        val nuevoPrecio = preciosList[spSeleccionarProducto.selectedItemPosition]
        val nuevoTotal = nuevoPrecio * nuevaCantidad
        val nuevoCliente = etClienteNombre.text.toString()

        val valores = ContentValues().apply {
            put("fecha", nuevaFecha)
            put("vendedor", nuevoCajero)
            put("cantidad", nuevaCantidad)
            put("precio", nuevoPrecio)
            put("total", nuevoTotal)
            put("cliente", nuevoCliente)
        }

        db.update("ventas", valores, "id=?", arrayOf(idVenta.toString()))
        limpiarCampos()
        mostrarVentas()
    }

    private fun buscarVenta() {
        val idVenta = etCantidad.text.toString().toIntOrNull() ?: return

        val cursor = db.rawQuery("SELECT * FROM ventas WHERE id=?", arrayOf(idVenta.toString()))
        if (cursor.moveToFirst()) {
            etClienteNombre.setText(cursor.getString(cursor.getColumnIndex("cliente")))
            etFecha.setText(cursor.getString(cursor.getColumnIndex("fecha")))
            spCajeros.setSelection(cajerosList.indexOf(cursor.getString(cursor.getColumnIndex("vendedor"))))
            etCantidad.setText(cursor.getString(cursor.getColumnIndex("cantidad")))
            spSeleccionarProducto.setSelection(preciosList.indexOf(cursor.getDouble(cursor.getColumnIndex("precio"))))
        }
        cursor.close()
    }

    private fun eliminarVenta() {
        val idVenta = etCantidad.text.toString().toIntOrNull() ?: return

        db.delete("ventas", "id=?", arrayOf(idVenta.toString()))
        limpiarCampos()
        mostrarVentas()
    }

    private fun limpiarCampos() {
        etCodigoVenta.text.clear()
        etClienteNombre.text.clear()
        etFecha.text.clear()
        etCantidad.text.clear()
        tvTotalProducto.text = "Total: S/"
    }

    private fun mostrarVentas() {
        tbVentas.removeAllViews()

        val encabezadoRow = TableRow(this)
        encabezadoRow.addView(crearTexto("Código de Venta"))
        encabezadoRow.addView(crearTexto("Producto"))
        encabezadoRow.addView(crearTexto("Vendedor"))
        encabezadoRow.addView(crearTexto("Cantidad"))
        encabezadoRow.addView(crearTexto("Precio"))
        tbVentas.addView(encabezadoRow)

        val cursor = db.rawQuery("SELECT * FROM ventas", null)
        if (cursor.count == 0) {
            Toast.makeText(this, "No hay ventas registradas", Toast.LENGTH_SHORT).show()
        } else {
            while (cursor.moveToNext()) {
                val fila = TableRow(this)
                fila.addView(crearTexto(cursor.getString(cursor.getColumnIndex("id"))))
                fila.addView(crearTexto(cursor.getString(cursor.getColumnIndex("producto"))))
                fila.addView(crearTexto(cursor.getString(cursor.getColumnIndex("vendedor"))))
                fila.addView(crearTexto(cursor.getString(cursor.getColumnIndex("cantidad"))))
                fila.addView(crearTexto(cursor.getString(cursor.getColumnIndex("precio"))))
                tbVentas.addView(fila)
            }
        }
        cursor.close()
    }

    private fun crearTexto(texto: String): TextView {
        val textView = TextView(this)
        textView.text = texto
        textView.textSize = 16f
        textView.setPadding(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        textView.gravity = Gravity.CENTER
        textView.setBackgroundColor(getColor(android.R.color.white)) // Fondo blanco para legibilidad
        textView.setTextColor(getColor(android.R.color.black)) // Texto negro para contraste
        return textView
    }


    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
