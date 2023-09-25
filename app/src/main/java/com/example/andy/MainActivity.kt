package com.example.andy

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.andy.clases.Instancias
import com.example.andy.clases.MisImagenes
import com.example.andy.clases.conexionInertnet
import com.example.andy.clases.usuarios
import com.example.andy.databinding.ActivityMainBinding
import com.example.andy.sqlite.DataManager
import com.example.andy.sqlite.DatabaseHelper
import com.example.andy.sqlite.usuariosSqlite
import com.example.andy.ui.home.HomeFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.util.Arrays
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var dialogUsuario: Dialog
    //autenticacion
    var providers: MutableList<AuthUI.IdpConfig?>? = null
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private val MY_REQUEST_CODE = 200
    private lateinit var instancias: Instancias

    private lateinit var imageViewProfile: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var dataManager: DataManager
    private val conexion = conexionInertnet()
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogUsuario = Dialog(this)
        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        dataManager = DataManager(this)
        instancias = Instancias()
        val drawerLayout: DrawerLayout = binding.drawerLayout
        // Bloquear el Navigation Drawer
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val view = navView.getHeaderView(0)
        //instancias
        imageViewProfile = view.findViewById(R.id.imageView)
        txtName = view.findViewById(R.id.nombre)
        txtEmail = view.findViewById(R.id.correo)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_login, R.id.nav_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null
        //autenticacion
        //Firebase

        providers = Arrays.asList(
            // AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Habilitar Smart Lock
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            // Tiene acceso a Internet
            if (requestCode == MY_REQUEST_CODE) {
                    // El usuario ha iniciado sesión exitosamente
                    currentUser = mAuth!!.currentUser
                    //showMessaje("todo bien")
                    // Resto del código para manejar el usuario autenticado
                    //pedir uduario y contraseña
                    dialogUsuario.setContentView(R.layout.dialog_register)
                    dialogUsuario.setCanceledOnTouchOutside(false)
                    val txtInputnombre = dialogUsuario.findViewById<TextInputLayout>(R.id.layoutNombre_Registrado)
                    val txtInputContrsena = dialogUsuario.findViewById<TextInputLayout>(R.id.layoutNombre_ConfirmarContraseña)
                    val usernameEditText = dialogUsuario.findViewById<TextInputEditText>(R.id.usernameEditText)
                    val passwordEditText = dialogUsuario.findViewById<TextInputEditText>(R.id.passwordEditText)
                    val confirmPasswordEditText = dialogUsuario.findViewById<TextInputEditText>(R.id.passwordEditTextconfrimar)
                    val btnRegistrar = dialogUsuario.findViewById<MaterialButton>(R.id.botonGuardar)
                    btnRegistrar.setOnClickListener {
                        val username = usernameEditText.text.toString()
                        val password = passwordEditText.text.toString()
                        val confirmPassword = confirmPasswordEditText.text.toString()

                        if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                            if (password == confirmPassword) {
                                if (password.length >= 6) { // Asegura que la contraseña tenga al menos 6 caracteres
                                    // Resto del código para crear el usuario y guardar en la base de datos
                                    // Insertar el nuevo usuario en la tabla Usuario
                                    val dataRefe =
                                        instancias.referenciaInformacionDelUsuario(currentUser!!.uid)
                                    val currentDateTimeString =
                                        DateFormat.getDateTimeInstance().format(Date())
                                    val u = usuarios(
                                        currentUser!!.uid,
                                        currentUser!!.email.toString(),
                                        currentUser!!.displayName.toString(),
                                        currentUser!!.photoUrl.toString(),
                                        currentDateTimeString
                                    )

                                    val imageUrl = currentUser!!.photoUrl.toString()
                                    try {
                                        Glide.with(this@MainActivity)
                                            .asBitmap()
                                            .load(imageUrl)
                                            .into(object : SimpleTarget<Bitmap>() {
                                                override fun onResourceReady(
                                                    bitmap: Bitmap,
                                                    transition: Transition<in Bitmap>?
                                                ) {
                                                    val outputStream = ByteArrayOutputStream()
                                                    bitmap.compress(
                                                        Bitmap.CompressFormat.JPEG,
                                                        100,
                                                        outputStream
                                                    )
                                                    val photoByteArray = outputStream.toByteArray()
                                                    val usuarioSQl = usuariosSqlite(
                                                        currentUser!!.uid,
                                                        photoByteArray,
                                                        currentUser!!.displayName.toString(),
                                                        currentUser!!.email.toString(),
                                                        username,
                                                        password
                                                    )
                                                    dataRefe.setValue(u) { error, ref ->
                                                        uploadImage(
                                                            currentUser!!.photoUrl.toString(),
                                                            imageViewProfile
                                                        )
                                                        txtName.text = currentUser!!.displayName
                                                        txtEmail.text = currentUser!!.email
                                                        dataManager.verificarSiElUsarioExiste(usuarioSQl)
                                                        // showMessaje("Usuario Registrado")
                                                        dialogUsuario.dismiss()
                                                    }

                                                }
                                            })
                                    } catch (e: Exception) {
                                        e.printStackTrace()

                                    }

                                } else {
                                    passwordEditText.error =
                                        "La contraseña debe tener al menos 6 caracteres"
                                }

                            } else {
                                confirmPasswordEditText.error = "Las contraseñas no coinciden"
                                showMessaje("Las contraseñas no coinciden")
                            }
                        } else {
                            showMessaje("Campos Vacíos")
                        }

                    }
                    dialogUsuario.show()

            }else{
                finishAffinity()
            }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection based on the item ID
        when (item.itemId) {
            R.id.action_settings -> {
                // Código para manejar la selección del ítem 1
               // SubirArchivos()
                metodoSalir()
                return true
            }
            // Agrega más casos para otros ítems del menú si es necesario
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        currentUser = mAuth!!.currentUser
        if (currentUser != null) {
         val array  = dataManager.InfoUsuario()
          //  showMessaje("tamaño array ${array.size}")
            for (usuario in array) {
                println("contraseña ${usuario.contrasena} usuario ${usuario.usuario}")
            }
                val name = currentUser!!.displayName
                val email = currentUser!!.email
                val photoUrl = currentUser!!.photoUrl.toString()
                val uid = currentUser!!.uid
                val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
            val dataRefe = instancias.referenciaInformacionDelUsuario(currentUser!!.uid)
            val u = usuarios(currentUser!!.uid, currentUser!!.email.toString(), currentUser!!.displayName.toString(), currentUser!!.photoUrl.toString(), currentDateTimeString)
            dataRefe.setValue(u) { error, ref ->

            }
            uploadImage(currentUser!!.photoUrl.toString(),imageViewProfile)
            txtName.text = currentUser!!.displayName
            txtEmail.text = currentUser!!.email
        }else {
            muestraOpciones()
        }
    }
    fun muestraOpciones() {
        mainScope.launch {
            val isConnected = withContext(Dispatchers.IO) {
                conexion.isInternetAvailable(this@MainActivity)
            }

            if (isConnected) {
                // Tiene acceso a Internet
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers!!)
                        .build(),MY_REQUEST_CODE
                )
            } else {
                // No tiene acceso a Internet
                showMessaje("No tiene acceso a Internet")
                finish()
            }
        }
    }
    private fun uploadImage(urlImagen: String, imageViewProfile: ImageView) {
            // Cargando la imagen en la ImageView
            Picasso.get().load(urlImagen).into(imageViewProfile)

    }

    fun metodoSalir() {
        val databaseHelper = DatabaseHelper(applicationContext)
        databaseHelper.borrarBaseDeDatosCompleta()
            AuthUI.getInstance()
                .signOut(applicationContext)
                .addOnCompleteListener {// muestraOpciones()
                }.addOnFailureListener { e -> Toast.makeText(
                    applicationContext, ""
                            + e.message, Toast.LENGTH_LONG
                ).show()
                }

        finishAffinity() // Cierra todas las actividades de la aplicación

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val fragmento = HomeFragment()

      /*  supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragmento)
            .addToBackStack(null) // Agregar esta línea para administrar la pila trasera
            .commit()*/

    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel() // Importante: Cancelar las coroutines al destruir la actividad
    }

private fun showMessaje(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show()
    }
}