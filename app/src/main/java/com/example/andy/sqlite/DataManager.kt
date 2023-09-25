package com.example.andy.sqlite

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.widget.Toast

import com.example.andy.clases.MisImagenes
import com.example.andy.clases.usuariosAcceso

class DataManager(context: Context) {
    val context: Context = context
    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)

    private fun insertInfor(
        uid: String,
        photo: ByteArray,
        nombre: String,
        fechaHora: String
    ) {
        val values = ContentValues()
        //values.put(DatabaseHelper.COLUMN_ID,id)
        values.put(DatabaseHelper.COLUMN_UID, uid)
        values.put(DatabaseHelper.COLUMN_PHOTO, photo)
        values.put(DatabaseHelper.COLUMN_NAME, nombre)
        values.put(DatabaseHelper.COLUMN_FECHA_HORA, fechaHora)

        val db = databaseHelper.writableDatabase
        db.insert(DatabaseHelper.TABLE_NAME, null, values)
        db.close()
        showMessaje("Datos Guardados")

    }


    private fun insertInforDescarga(
        id: Int,
        uid: String,
        photo: ByteArray,
        nombre: String,
        fechaHora: String,progressDialog: ProgressDialog
    ) {
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_ID, id.toLong()) // Convirtiendo el ID a Long
        values.put(DatabaseHelper.COLUMN_UID, uid)
        values.put(DatabaseHelper.COLUMN_PHOTO, photo)
        values.put(DatabaseHelper.COLUMN_NAME, nombre)
        values.put(DatabaseHelper.COLUMN_FECHA_HORA, fechaHora)

        val db = databaseHelper.writableDatabase

        try {
            // Insertar los valores en la tabla
            db.insert(DatabaseHelper.TABLE_NAME, null, values)
            showMessaje("Descargado")
            progressDialog.dismiss()
        } catch (e: Exception) {
            // Manejar la excepción en caso de error
            showMessaje("Error al guardar los datos: ${e.message}")
        } finally {
            db.close() // Cerrar la base de datos
        }
    }

    fun InsertarImagenesDescarga(info: MisImagenes, progressDialog: ProgressDialog) {
        val isUsuarioTableExists = databaseHelper.isTableExists(DatabaseHelper.TABLE_NAME)
        if (isUsuarioTableExists) {
            insertInforDescarga(info.id,info.uid, info.photo!!, info.nombreImagen, info.fechaHora,progressDialog)

        } else {
            println("no existe la tabla")
            databaseHelper.onCreate(databaseHelper.writableDatabase) // Crear la tabla
            insertInforDescarga(info.id,info.uid, info.photo!!, info.nombreImagen, info.fechaHora,progressDialog)
        }
    }
    fun InsertarImagenes(info: MisImagenes) {
        val isUsuarioTableExists = databaseHelper.isTableExists(DatabaseHelper.TABLE_NAME)
        if (isUsuarioTableExists) {
            insertInfor(info.uid, info.photo!!, info.nombreImagen, info.fechaHora)

        } else {
            println("no existe la tabla")
            databaseHelper.onCreate(databaseHelper.writableDatabase) // Crear la tabla
            insertInfor(info.uid, info.photo!!, info.nombreImagen, info.fechaHora)
        }
    }


    fun deleteAllTablas() {
        val db = databaseHelper.writableDatabase
        try {
            db.beginTransaction() // Comenzar una transacción
            db.delete(DatabaseHelper.TABLE_NAME, null, null)
            db.delete(DatabaseHelper.TABLE_NAME_USUARIOS, null, null)
            db.setTransactionSuccessful() // Marcar la transacción como exitosa
            println("Tablas eliminadas")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction() // Finalizar la transacción, independientemente de si fue exitosa o no
            db.close()
        }
    }

    fun deleteRegistroById(id: Long) {
        val db = databaseHelper.writableDatabase
        try {
            db.beginTransaction()

            val whereClause = "${DatabaseHelper.COLUMN_ID} = ?"
            val whereArgs = arrayOf(id.toString())

            db.delete(DatabaseHelper.TABLE_NAME, whereClause, whereArgs)

            db.setTransactionSuccessful()
            println("Registro eliminado con ID $id")
            showMessaje("Registro eliminado con ID $id")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun updateNombreById(id: Long, nuevoNombre: String) {
        val db = databaseHelper.writableDatabase
        try {
            db.beginTransaction()

            val values = ContentValues()
            values.put(DatabaseHelper.COLUMN_NAME, nuevoNombre)

            val whereClause = "${DatabaseHelper.COLUMN_ID} = ?"
            val whereArgs = arrayOf(id.toString())

            db.update(DatabaseHelper.TABLE_NAME, values, whereClause, whereArgs)

            db.setTransactionSuccessful()
            println("Nombre actualizado para el registro con ID $id")
            showMessaje("Nombre actualizado para el registro con ID $id")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }


   /* @SuppressLint("Range", "SuspiciousIndentation")
    fun misImagenes(context: Context): ArrayList<MisImagenes> {
        val db = databaseHelper.readableDatabase
        val datosUsuario = mutableListOf<MisImagenes>()
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)

        val totalRows = cursor.count
        // var loadedRows = 0
//showMessaje(totalRows.toString() + cursor.count)
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)).toInt()
            val uid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_UID))
            val foto = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHOTO))
            val nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
            val fechaHora = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FECHA_HORA))

            val info = MisImagenes(id, uid, foto, nombre, fechaHora)
            datosUsuario.add(info)
        }
        return datosUsuario as ArrayList<MisImagenes>
    }*/
   fun misImagenes(context: Context): ArrayList<MisImagenes> {
       val db = databaseHelper.readableDatabase
       val datosUsuario = ArrayList<MisImagenes>()

       val columns = arrayOf(
           DatabaseHelper.COLUMN_ID,
           DatabaseHelper.COLUMN_UID,
           DatabaseHelper.COLUMN_PHOTO,
           DatabaseHelper.COLUMN_NAME,
           DatabaseHelper.COLUMN_FECHA_HORA
       )

       val cursor = db.query(
           DatabaseHelper.TABLE_NAME,
           columns,
           null,
           null,
           null,
           null,
           null
       )

       cursor?.use { cursor ->
           val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
           val uidIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_UID)
           val photoIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHOTO)
           val nombreIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)
           val fechaHoraIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_FECHA_HORA)

           while (cursor.moveToNext()) {
               val id = cursor.getInt(idIndex)
               val uid = cursor.getString(uidIndex)
               val fotoByteArray = cursor.getBlob(photoIndex)
               val nombre = cursor.getString(nombreIndex)
               val fechaHora = cursor.getString(fechaHoraIndex)

               val info = MisImagenes(id, uid, fotoByteArray, nombre, fechaHora)
               datosUsuario.add(info)
           }
       }

       cursor?.close()
       db.close()

       return datosUsuario
   }

    @SuppressLint("Range", "SuspiciousIndentation")
    fun InfoUsuario(): ArrayList<usuariosSqlite> {
        val db = databaseHelper.readableDatabase
        val datosUsuario = mutableListOf<usuariosSqlite>()
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME_USUARIOS}"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val uid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_USUARIOS))
            val foto = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_FOTO_U))
            val nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE))
            val correo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CORREO))
            val usuario = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USUARIO))
            val contrasena = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTRASENA))

            val info = usuariosSqlite(uid, foto, nombre, correo,usuario,contrasena)
            datosUsuario.add(info)
        }
        return datosUsuario as ArrayList<usuariosSqlite>
    }


    private fun showMessaje(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }
    fun existeLogin(): Boolean {
        val db = databaseHelper.readableDatabase
        val tableName = DatabaseHelper.TABLE_NAME_LOGIN

        val countQuery = "SELECT COUNT(*) FROM $tableName"
        val cursor = db.rawQuery(countQuery, null)

        var recordCount = 0

        try {
            if (cursor != null && cursor.moveToFirst()) {
                recordCount = cursor.getInt(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        val existeRegistro = recordCount > 0
        if (existeRegistro) {
            //showMessaje("Existen registros en la tabla $tableName")
            return true
        } else {
           // showMessaje("No existen registros en la tabla $tableName")
            return false
        }

        return existeRegistro
    }


    fun RegistrarUsuarioAcceso(usuario: usuariosAcceso){
            val values = ContentValues()
            //values.put(DatabaseHelper.COLUMN_ID,id)
            values.put(DatabaseHelper.COLUMN_ID_LOGIN, usuario.usuario)
            values.put(DatabaseHelper.COLUMN_CONTRASENA_LOGIN, usuario.contrasena)
            val db = databaseHelper.writableDatabase
            db.insert(DatabaseHelper.TABLE_NAME_LOGIN, null, values)
            db.close()
            showMessaje("Registrado")

    }

    fun iniciarSesionLogin(username: String, password: String): Boolean {
        val db = databaseHelper.readableDatabase
        val selection = "${DatabaseHelper.COLUMN_ID_LOGIN} = ? AND ${DatabaseHelper.COLUMN_CONTRASENA_LOGIN} = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(
            DatabaseHelper.TABLE_NAME_LOGIN,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val credentialsMatch = cursor.count > 0
        cursor.close()
        db.close()
        return credentialsMatch
    }


    fun verificarSiElUsarioExiste(usuario: usuariosSqlite) {
        val isUsuarioTableExists = databaseHelper.isTableExists(DatabaseHelper.TABLE_NAME_USUARIOS)
        if (isUsuarioTableExists) {
            println("la tabla si existe ${usuario.nombre} ${usuario.usuario}")
            val db = databaseHelper.writableDatabase
            val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME_USUARIOS} WHERE ${DatabaseHelper.COLUMN_ID_USUARIOS} = ?"
            val cursor = db.rawQuery(query, arrayOf(usuario.uid.toString()))
            if (cursor.moveToFirst()) {
                println("el usuario si se encuentra ${usuario.nombre} ${usuario.usuario}")

            } else {
                println("el usuario no se encuentra")
                InsertarUsuario(usuario)
            }

            cursor.close()
            db.close()
        } else {
            // La tabla "Usuario" no existe en la base de datos
             println("La tabla \"Usuario\" no existe en la base de datos")
            databaseHelper.onCreate(databaseHelper.writableDatabase) // Crear la tabla
            InsertarUsuario(usuario)
        }
    }
    fun InsertarUsuario(usuario: usuariosSqlite) {
        val values = ContentValues()
        //values.put(DatabaseHelper.COLUMN_ID,id)
        values.put(DatabaseHelper.COLUMN_ID_USUARIOS, usuario.uid)
        values.put(DatabaseHelper.COLUMN_FOTO_U, usuario.foto)
        values.put(DatabaseHelper.COLUMN_NOMBRE, usuario.nombre)
        values.put(DatabaseHelper.COLUMN_CORREO, usuario.correo)
        values.put(DatabaseHelper.COLUMN_USUARIO, usuario.usuario)
        values.put(DatabaseHelper.COLUMN_CONTRASENA, usuario.contrasena)

        val db = databaseHelper.writableDatabase
        db.insert(DatabaseHelper.TABLE_NAME_USUARIOS, null, values)
        db.close()
        showMessaje("Registrado")
    }

    fun checkCredentials(username: String, password: String): Boolean {
        val db = databaseHelper.readableDatabase
        val selection = "${DatabaseHelper.COLUMN_USUARIO} = ? AND ${DatabaseHelper.COLUMN_CONTRASENA} = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(
            DatabaseHelper.TABLE_NAME_USUARIOS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val credentialsMatch = cursor.count > 0
        cursor.close()
        db.close()
        return credentialsMatch
    }

}