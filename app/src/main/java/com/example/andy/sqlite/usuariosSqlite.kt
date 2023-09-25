package com.example.andy.sqlite

class usuariosSqlite(uid: String, foto: ByteArray?,
    nombre: String,
    correo:String,usuario:String,contrasena:String) {

    var uid: String = uid
    var nombre: String = nombre
    var foto: ByteArray = foto!!
    var correo: String = correo
    var usuario:String = usuario
    var contrasena :String = contrasena

    constructor() : this("",null,"","","","")

}