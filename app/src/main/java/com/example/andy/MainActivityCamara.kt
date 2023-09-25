package com.example.andy

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.andy.clases.MisImagenes
import com.example.andy.sqlite.DataManager
import com.example.andy.ui.gallery.GalleryFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.util.Date

class MainActivityCamara : AppCompatActivity() {
    private val CAMERA_PERMISSION_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val GALLERY_REQUEST_CODE = 103
    private lateinit var openCameraButton: ImageView
    private lateinit var buttonGuardar: Button
    private  var byteArray: ByteArray? = null
    private  var byteArrayLength: Int = 0
    private lateinit var editTextnombre: EditText

    private lateinit var alertDialog: AlertDialog
    private lateinit var dataManager: DataManager
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_camara)

        openCameraButton = findViewById(R.id.imageViewFoto)
        dataManager = DataManager(this)
        mAuth = FirebaseAuth.getInstance()
        openCameraButton.setOnClickListener {
            showOptionsDialog()
        }

        editTextnombre = findViewById(R.id.editextNombre)
        buttonGuardar = findViewById(R.id.buttonGuardarFoto)
        buttonGuardar.setOnClickListener {
            currentUser = mAuth!!.currentUser
            if (byteArray == null){
                showMessaje("Por favor selecciona una Imagen ")
            }else{

                //showMessaje("Foto A guradar $byteArrayLength ")
                val nombre = editTextnombre.text.toString()
                val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())

                if (nombre.isNotEmpty()){
                    //showMessaje("con nombre $nombre")
                    val misImagenes: MisImagenes
                    misImagenes = MisImagenes(1,currentUser!!.uid,byteArray, nombre,currentDateTimeString)
                    dataManager.InsertarImagenes(misImagenes)
                    editTextnombre.setText("")
                    restoreOriginalImage()
                }else{
                    showMessaje("Por favor Ingrese el nombre")
                }


            }
        }
    }

    private fun restoreOriginalImage() {
        openCameraButton.setImageResource(R.drawable.subir)

    }

    private fun showMessaje(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show()
    }

    private fun showOptionsDialog() {
        val options = arrayOf("Tomar Foto", "Seleccionar de Galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elija una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkCameraPermission()
                1 -> checkGaleriaPermission()
            }
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun checkGaleriaPermission(){
        // Verificar si tenemos permiso de lectura
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si no tenemos permiso, solicitarlo
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_REQUEST_CODE)
        } else {
            // Si ya tenemos permiso, continuar con la lógica de selección de imagen
            openGallery()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera() // Asegúrate de llamar a openCamera() aquí
            } else {
                // Manejo de permisos denegados
                showMessaje("Permiso Denegado")
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE) { // 1 es el identificador de solicitud que usamos al solicitar el permiso de lectura
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de lectura otorgado, puedes abrir la galería y seleccionar una imagen aquí
                openGallery()
            } else {
                // Permiso de lectura denegado
                showMessaje("Permiso Denegado para acceder a la galería")
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                  //  showMessaje("entro")
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    openCameraButton.setImageBitmap(imageBitmap)
                    byteArray = covertirByteArray(imageBitmap)
                    byteArrayLength = byteArray!!.size
                    //println("Tamaño del arreglo de bytes: $byteArrayLength bytes")
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data?.data
                    openCameraButton.setImageURI(selectedImageUri)
                    // Convertir imagen seleccionada a ByteArray
                    val selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        selectedImageUri
                    )
                    byteArray = covertirByteArray(selectedImageBitmap)
                    byteArrayLength = byteArray!!.size
                   // println("Tamaño del arreglo de bytes: $byteArrayLength bytes")
                }
            }
        }
    }

    private fun covertirByteArray(imageBitmap: Bitmap): ByteArray{
        // Convertir imagen a ByteArray
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray: ByteArray = outputStream.toByteArray()
        return byteArray
    }

    /*override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as? NavHostFragment
        val navController = navHostFragment?.navController
        val currentDestination = navController?.currentDestination

        if (currentDestination?.id == R.id.nav_gallery) {
            val galleryFragment = navHostFragment.childFragmentManager.primaryNavigationFragment as? GalleryFragment
         //   galleryFragment?.onBackPressed()
        } else {
            super.onBackPressed()
            //showMessaje("entro")
        }
    }*/


}