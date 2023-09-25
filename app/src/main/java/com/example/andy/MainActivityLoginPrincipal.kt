package com.example.andy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.andy.clases.usuariosAcceso
import com.example.andy.sqlite.DataManager
import com.example.andy.ui.login.LoggedInUserView
import com.example.andy.ui.login.LoginResult
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivityLoginPrincipal : AppCompatActivity() {
    lateinit var button :MaterialButton
    private lateinit var dataManager: DataManager
    private lateinit var LayoutconfirmaContra: TextInputLayout
    private lateinit var LayoutContra: TextInputLayout
    private lateinit var LayoutUsuario: TextInputLayout

    private lateinit var editexUsuario: TextInputEditText
    private lateinit var editexContra: TextInputEditText
    private lateinit var editexConfirmarContra: TextInputEditText

    var ban: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login_principal)
        dataManager = DataManager(this)
        button = findViewById(R.id.botonIniciar_login)
        LayoutconfirmaContra = findViewById(R.id.layout_ConfirmarContraseña_login)
        LayoutContra = findViewById(R.id.layout_Contraseña_login)
        LayoutUsuario = findViewById(R.id.layout_usuario_login)

        editexUsuario = findViewById(R.id.usernameEditTextLogin)
        editexContra = findViewById(R.id.passwordEditText_login)
        editexConfirmarContra = findViewById(R.id.passwordEditText_confrimar_login)
        ban = dataManager.existeLogin()
        if (ban == true){
            button.text = "Iniciar Sesión"
            LayoutconfirmaContra.visibility = View.GONE
        }else{
            button.text = "Registrar"
            LayoutconfirmaContra.visibility = View.VISIBLE
        }


        button.setOnClickListener {
            if (ban == true){
               /* button.text = "Iniciar Sesión"
                LayoutconfirmaContra.visibility = View.GONE*/
                IniciarSesion()
                //showMensaje("registrado $ban")
            }else{
                //showMensaje("usuario no existe")
                RegistarUsuario()
               // showMensaje("no registrado $ban")
            }
        }


    }
    private fun showMensaje(mensaje:String){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
    }

    fun IniciarSesion(){
        val usuario = editexUsuario.text.toString()
        val contra = editexContra.text.toString()

        if (usuario.isNotEmpty()){
            LayoutUsuario.error = null
            if (contra.isNotEmpty()){
                LayoutContra.error = null
                val credentialsMatch = dataManager.iniciarSesionLogin(usuario, contra)

                if (credentialsMatch) {
                    LayoutUsuario.error = null
                    LayoutContra.error = null
                    showMensaje("Bienvenido")
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                } else {
                   LayoutUsuario.error = "Error de Usuario"
                    LayoutContra.error = "Error de Usuario"
                    finish()
                }


          }else{
              LayoutContra.error = "Campo Requerido"
          }
        }else{
            LayoutUsuario.error = "Campo Requerido"
        }

            // showMensaje("usuario si existe")


    }
    @SuppressLint("SuspiciousIndentation")
    fun RegistarUsuario(){
        val usuario = editexUsuario.text.toString()
        val contra = editexContra.text.toString()
        val confirmaContra = editexConfirmarContra.text.toString()
            if (usuario.isNotEmpty()) {
                LayoutUsuario.error = null
                if (contra.isNotEmpty()){
                    LayoutContra.error = null
                    if (confirmaContra.isNotEmpty()){
                        LayoutconfirmaContra.error = null
                        if (contra ==  confirmaContra){
                          LayoutconfirmaContra.error = null
                            if (contra.length <= 5){
                             LayoutContra.error = "La contraseña Debe ser mayor a 5 digitos"
                            }else{
                                LayoutContra.error = null
                                LayoutconfirmaContra.error = null
                                LayoutUsuario.error = null
                                val us: usuariosAcceso
                                us = usuariosAcceso(usuario,contra)
                                dataManager.RegistrarUsuarioAcceso(us)
                                 val intent = Intent(this,MainActivity::class.java)
                                startActivity(intent)
                            }
                        }else{
                            LayoutconfirmaContra.error = "Las Contraseñas no Coinciden"
                        }
                    }else{
                        LayoutconfirmaContra.error = "Campo Requerido"
                    }
                    /* val intent = Intent(this,MainActivity::class.java)
                   startActivity(intent)*/
                }else{
                    LayoutContra.error = "Campo Requerido"
                }
            }else{
                LayoutUsuario.error = "Campo Requerido"
            }

    }
}