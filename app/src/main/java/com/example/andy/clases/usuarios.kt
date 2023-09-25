package com.example.andy.clases

class usuarios(uid: String, email: String, name: String, photo: String, currentDateTimeString: String) {
    var uid: String = uid
    var email: String = email
    var name: String = name
    var photo: String = photo
    var fechaHora: String = currentDateTimeString

    constructor() : this("","","","","")

}