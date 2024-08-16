package com.rico.evfinal

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProductosActivity : AppCompatActivity() {

    private lateinit var etIdProducto: EditText
    private lateinit var etProducto: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etPrecio: EditText
    private lateinit var btnNuevo: Button
    private lateinit var btnRegistrar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tbProductos: TableLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etIdProducto = findViewById(R.id.etIdProducto)
        etProducto = findViewById(R.id.etProducto)
        etCantidad = findViewById(R.id.etCantidad)
        etPrecio = findViewById(R.id.etPrecio)
        btnNuevo = findViewById(R.id.btnNueva)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnBuscar = findViewById(R.id.btnBuscarProducto)
        btnEliminar = findViewById(R.id.btnEliminar)
        tbProductos = findViewById(R.id.tbProductos)


        btnNuevo.setOnClickListener { limpiarCampos() }
        btnRegistrar.setOnClickListener { registrarProducto() }
        btnActualizar.setOnClickListener { actualizarProducto() }
        btnBuscar.setOnClickListener { buscarProducto() }
        btnEliminar.setOnClickListener { eliminarProducto() }

        cargarProductos()
    }


    private fun registrarProducto() {
        val idProducto = etIdProducto.text.toString().toInt()
        val producto = etProducto.text.toString()

        val cantidad = etCantidad.text.toString().toInt()
        val precio = etPrecio.text.toString()

        val admin = AdminSQLiteOpenHelper(this)
        val db: SQLiteDatabase = admin.writableDatabase
        val registro = ContentValues()

        registro.put("id_producto", idProducto)
        registro.put("producto", producto)
        registro.put("cantidad", cantidad)
        registro.put("precio", precio)
        db.insert("productos", null, registro)
        db.close()

        limpiarCampos()
        cargarProductos()
    }

    private fun buscarProducto() {
        val idProducto = etIdProducto.text.toString().toInt()
        val admin = AdminSQLiteOpenHelper(this)
        val db: SQLiteDatabase = admin.writableDatabase
        val fila: Cursor = db.rawQuery("select * from productos where id_producto=$idProducto", null)
        if (fila.moveToFirst()) {
            etProducto.setText(fila.getString(fila.getColumnIndex("producto")))
            etPrecio.setText(fila.getString(fila.getColumnIndex("precio")))
            etCantidad.setText(fila.getString(fila.getColumnIndex("cantidad")))

        } else {
            Toast.makeText(this, "No existe un producto con ese número", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    private fun actualizarProducto() {
        val idProducto = etIdProducto.text.toString().toInt()
        val producto = etProducto.text.toString()
        val cantidad = etCantidad.text.toString().toInt()
        val precio = etPrecio.text.toString()

        val admin = AdminSQLiteOpenHelper(this)
        val db: SQLiteDatabase = admin.writableDatabase
        val registro = ContentValues()

        registro.put("producto", producto)
        registro.put("cantidad", cantidad)
        registro.put("precio", precio)

        val cantidadActualizada = db.update("productos", registro, "id_producto=$idProducto", null)
        if (cantidadActualizada == 1) {
            Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No existe un pedido con ese número", Toast.LENGTH_SHORT).show()
        }
        db.close()

        limpiarCampos()
        cargarProductos()
    }

    private fun eliminarProducto() {
        val idProducto = etIdProducto.text.toString().toInt()
        val admin = AdminSQLiteOpenHelper(this)
        val db: SQLiteDatabase = admin.writableDatabase

        val cantidadEliminada = db.delete("productos", "id_producto=$idProducto", null)
        if (cantidadEliminada == 1) {
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No existe un producto con ese número", Toast.LENGTH_SHORT).show()
        }
        db.close()

        limpiarCampos()
        cargarProductos()
    }

    private fun limpiarCampos() {
        etIdProducto.text.clear()
        etProducto.text.clear()
        etCantidad.text.clear()
        etPrecio.text.clear()
    }

    private fun cargarProductos() {
        tbProductos.removeAllViews()
        val admin = AdminSQLiteOpenHelper(this)
        val db: SQLiteDatabase = admin.readableDatabase
        val cursor: Cursor = db.rawQuery("select id_producto, producto, cantidad from productos", null)

        while (cursor.moveToNext()) {
            val idProducto = cursor.getInt(0)
            val producto = cursor.getString(1)
            val stock = cursor.getString(2)

            val row = layoutInflater.inflate(R.layout.item_table_layout_pn, null)
            row.findViewById<TextView>(R.id.tvIdProducto).text = idProducto.toString()
            row.findViewById<TextView>(R.id.etProducto).text = producto
            row.findViewById<TextView>(R.id.etCantidad).text = stock

            tbProductos.addView(row)
        }
        cursor.close()
        db.close()
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
