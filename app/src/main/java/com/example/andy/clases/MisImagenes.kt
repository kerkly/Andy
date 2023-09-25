package com.example.andy.clases

class MisImagenes(id: Int, uid: String, photo: ByteArray?, nombreImagen: String, currentDateTimeString: String) {
    var id: Int = id
    var uid: String = uid
    var photo: ByteArray? = photo
    var nombreImagen: String = nombreImagen
    var fechaHora: String = currentDateTimeString

    constructor() : this(0,"",null,"","")
}