package com.rico.evfinal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(
    context,
    "productos_db",
    null,
    2  // Cambia la versión de la base de datos a 2 para que onUpgrade se ejecute
) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla productos
        db.execSQL(
            "CREATE TABLE productos (" +
                    "id_producto INTEGER PRIMARY KEY, " +
                    "producto TEXT, " +
                    "cantidad INTEGER, " +
                    "precio TEXT)"
        )

        // Crear tabla ventas
        db.execSQL(
            "CREATE TABLE ventas (" +
                    "id_venta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cliente TEXT, " +
                    "vendedor TEXT, " +
                    "fecha TEXT, " +
                    "cantidad INTEGER, " +
                    "total REAL, " +
                    "id_producto INTEGER, " +
                    "FOREIGN KEY(id_producto) REFERENCES productos(id_producto))"
        )

        // Crear tabla de usuarios
        db.execSQL(
            "CREATE TABLE usuariologin (" +
                    "id_user INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "nombre TEXT, " +
                    "celular TEXT, " +
                    "dni TEXT, " +
                    "username TEXT NOT NULL, " +
                    "clave_user TEXT NOT NULL, " +
                    "imagen BLOB)"  // Campo para almacenar la imagen del usuario
        )

        // Insertar usuarios en la tabla usuariologin (sin imágenes para estos usuarios de ejemplo)
        db.execSQL(
            "INSERT INTO usuariologin (nombre, celular, dni, username, clave_user) VALUES ('Nikson Benel Mendoza', '902374687', '75388696', 'admin', 'admin')"
        )
        db.execSQL(
            "INSERT INTO usuariologin (nombre, celular, dni, username, clave_user) VALUES ('Abraham Cueva Rico', '924575577', '60055345', 'rico', 'admin')"
        )

        // Insertar productos en la tabla productos
        db.execSQL(
            "INSERT INTO productos (id_producto, producto, cantidad, precio) VALUES (1, 'Tornillo', 100, 5.50)"
        )
        db.execSQL(
            "INSERT INTO productos (id_producto, producto, cantidad, precio) VALUES (2, 'Martillo', 50, 15.00)"
        )
        db.execSQL(
            "INSERT INTO productos (id_producto, producto, cantidad, precio) VALUES (3, 'Destornillador', 75, 7.25)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Modificar la tabla usuariologin para agregar el campo de imagen
            db.execSQL("ALTER TABLE usuariologin ADD COLUMN imagen BLOB")
        }
    }
}
