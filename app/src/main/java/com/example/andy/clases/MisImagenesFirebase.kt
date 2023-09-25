package com.example.andy.clases

class MisImagenesFirebase(id: Int, uid: String, photo: String, nombreImagen: String, currentDateTimeString: String) {
    var id: Int = id
    var uid: String = uid
    var photo: String = photo
    var nombreImagen: String = nombreImagen
    var fechaHora: String = currentDateTimeString

    constructor() : this(0,"","","","")
}