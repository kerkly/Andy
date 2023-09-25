package com.example.andy.clases

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Instancias() {
    val database = FirebaseDatabase.getInstance()
    private val usuario = "Usuarios"
    private val misDatos = "MisDatos"
    private val Backup = "Backup"

    fun referenciaInformacionDelUsuario(id: String): DatabaseReference{
        val databaseReferenceMisDatos = database.getReference(usuario).child(id).child(misDatos)
        return databaseReferenceMisDatos
    }
    fun referenciaParaLosArchivos(id: String): DatabaseReference{
        val databaseReference =  database.getReference(usuario).child(id).child(Backup)
        return databaseReference
    }
}