package com.example.andy.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
     val context: Context = context
    companion object {
        private const val DATABASE_NAME = "Andy.db"
        private const val DATABASE_VERSION = 1

        // Define la estructura de la tabla
         const val TABLE_NAME = "listaImagenes"
         const val COLUMN_ID = "id"
         const val COLUMN_UID = "UidUsuario"
        const val COLUMN_PHOTO = "photo"
         const val COLUMN_NAME = "nombreImagen"
        const val COLUMN_FECHA_HORA = "fechaHora"

        //usuario
        //segunda tabla
        const val TABLE_NAME_USUARIOS = "Usuario"
        const val COLUMN_ID_USUARIOS = "uid"
        const val COLUMN_FOTO_U = "foto"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_USUARIO = "usuario"
        const val COLUMN_CONTRASENA = "contrasena"

        //acceso
        const val TABLE_NAME_LOGIN = "Login"
        const val COLUMN_ID_LOGIN = "usuario"
        const val COLUMN_CONTRASENA_LOGIN = "contrasena"

    }


    override fun onCreate(db: SQLiteDatabase?) {
        // Crea la tabla
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_UID TEXT, $COLUMN_PHOTO BLOB, $COLUMN_NAME TEXT, $COLUMN_FECHA_HORA TEXT)"
        db?.execSQL(createTableQuery)

        val createTableUSer = "CREATE TABLE $TABLE_NAME_USUARIOS($COLUMN_ID_USUARIOS TEXT PRIMARY KEY, $COLUMN_NOMBRE TEXT, $COLUMN_FOTO_U BLOB,$COLUMN_CORREO TEXT, $COLUMN_USUARIO TEXT, $COLUMN_CONTRASENA TEXT)"
        db?.execSQL(createTableUSer)

        val createTabalaLogin = "CREATE TABLE $TABLE_NAME_LOGIN($COLUMN_ID_LOGIN TEXT PRIMARY KEY, $COLUMN_CONTRASENA_LOGIN TEXT)"
        db?.execSQL(createTabalaLogin)
    }
    fun borrarBaseDeDatosCompleta() {
        context.deleteDatabase(DATABASE_NAME)
        println("Base de datos eliminada")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Si necesitas realizar cambios en la estructura de la base de datos
        // puedes implementar la lógica de actualización aquí
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Si necesitas revertir a una versión anterior de la base de datos
        // puedes implementar la lógica de degradación aquí
    }

    fun isTableExists(tableName: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$tableName'", null)
        val tableExists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return tableExists
    }

}