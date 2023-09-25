package com.example.andy.ui.slideshow

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.andy.clases.Instancias
import com.example.andy.clases.MisImagenes
import com.example.andy.clases.MisImagenesFirebase
import com.example.andy.clases.conexionInertnet
import com.example.andy.databinding.FragmentSlideshowBinding
import com.example.andy.sqlite.DataManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var btnSubir : MaterialButton
    private lateinit var btnDescargar: MaterialButton
    private lateinit var dataManager: DataManager
    private lateinit var instancias: Instancias
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    private val conexion = conexionInertnet()
    private val mainScope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        btnDescargar = binding.btnDescargar
        btnSubir = binding.btnSubir
        dataManager = DataManager(requireContext())
        instancias = Instancias()
        mAuth = FirebaseAuth.getInstance()
        btnSubir.setOnClickListener {
            SubirArchivos()
        }
        btnDescargar.setOnClickListener {
            DescargarArchivos()
        }
        return root
    }

   /* private fun DescargarArchivos() {
        currentUser = mAuth!!.currentUser
        val referencia = instancias.referenciaParaLosArchivos(currentUser!!.uid)
         val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Descargando archivos")
        progressDialog.setMessage("Por favor, espera...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.show()
        referencia.get().addOnSuccessListener { snapshot ->
                    println("mis id: ${snapshot.value}")
                    val dataMap = snapshot.value as? Map<String, Map<String, Any>>
                   // val datos = snapshot.getValue(MisImagenesFirebase::class.java)

                    if (dataMap != null) {
                        for ((key, imagenData) in dataMap) {
                            val nombreImagen = imagenData["nombreImagen"] as? String
                            val url = imagenData["phtoto"] as? String
                            val uid = imagenData["uid"] as? String
                            val fechaHora = imagenData["fechaHora"] as? String
                            val id = imagenData["id"] as? Int
                            try {
                                Glide.with(this@SlideshowFragment)
                                    .asBitmap()
                                    .load(url)
                                    .into(object : SimpleTarget<Bitmap>() {
                                        override fun onResourceReady(
                                            bitmap: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            // Aquí tienes el objeto Bitmap de la foto
                                            // Puedes continuar trabajando con el bitmap según tus necesidades
                                            // Por ejemplo, puedes convertir el Bitmap en un ByteArray
                                            val outputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                                            val photoByteArray = outputStream.toByteArray()
                                            val misImagenes = MisImagenes(
                                                key.toInt(),
                                                uid.toString(),
                                                photoByteArray,
                                                nombreImagen.toString(),
                                                fechaHora.toString()
                                            )
                                            dataManager.InsertarImagenesDescarga(misImagenes, progressDialog)

                                            println("ID: $key")
                                            println("UID: $uid")
                                            println("URL: $url")
                                            println("Nombre de la imagen: ${misImagenes.nombreImagen}")
                                            println("Fecha y hora: $fechaHora")
                                            println("ByteArray: ${photoByteArray} bytes")
                                          //  showMessaje("todo bien")

                                        }
                                    })
                            }catch (e:Exception){
                                e.printStackTrace()
                                progressDialog.dismiss()
                            }

                        }
                    } else {
                        progressDialog.dismiss()
                        println("No se encontraron datos")
                        showMessaje("No se encontraron datos ${currentUser!!.uid}")
                    }
    }.addOnFailureListener { error ->
        // Manejar el error en caso de que algo salga mal
    }
    }*/

    private fun DescargarArchivos() {
        mainScope.launch {
            val isConnected = withContext(Dispatchers.IO) {
                conexion.isInternetAvailable(requireContext())
            }

            if (isConnected) {
                // Tiene acceso a Internet
                currentUser = mAuth!!.currentUser
                val referencia = instancias.referenciaParaLosArchivos(currentUser!!.uid)

                val progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle("Descargando archivos")
                progressDialog.setMessage("Por favor, espera...")
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.isIndeterminate = true
                progressDialog.setCancelable(false)
                progressDialog.show()
                referencia.addListenerForSingleValueEvent (object : ValueEventListener {
                    // val datos = snapshot.getValue(MisImagenesFirebase::class.java)
                    override fun onDataChange(snapshot: DataSnapshot) {
                        println("mis id: ${snapshot.value}")
                        val dataList = mutableListOf<MisImagenesFirebase>()

                        for (childSnapshot in snapshot.children) {
                            val imagenData = childSnapshot.getValue(MisImagenesFirebase::class.java)
                            imagenData?.let {
                                dataList.add(imagenData)
                            }
                        }

                        // Aquí dataList contiene solo objetos válidos, sin nulls
                        for (imagenData in dataList) {
                            // Accede a los campos de cada imagenData
                            val id = imagenData.id
                            val uid = imagenData.uid
                            val photo = imagenData.photo
                            val nombreImagen = imagenData.nombreImagen
                            val fechaHora = imagenData.fechaHora
                            println("todo bien $nombreImagen")
                            try {
                                Glide.with(this@SlideshowFragment)
                                    .asBitmap()
                                    .load(photo)
                                    .into(object : SimpleTarget<Bitmap>() {
                                        override fun onResourceReady(
                                            bitmap: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            // Aquí tienes el objeto Bitmap de la foto
                                            // Puedes continuar trabajando con el bitmap según tus necesidades
                                            // Por ejemplo, puedes convertir el Bitmap en un ByteArray
                                            val outputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                                            val photoByteArray = outputStream.toByteArray()
                                            val misImagenes = MisImagenes(
                                                id.toInt(),
                                                uid.toString(),
                                                photoByteArray,
                                                nombreImagen.toString(),
                                                fechaHora.toString()
                                            )
                                            dataManager.InsertarImagenesDescarga(misImagenes, progressDialog)

                                            println("ID: $id")
                                            println("UID: $uid")
                                            println("URL: $photo")
                                            println("Nombre de la imagen: ${misImagenes.nombreImagen}")
                                            println("Fecha y hora: $fechaHora")
                                            println("ByteArray: ${photoByteArray} bytes")
                                            //  showMessaje("todo bien")

                                        }
                                    })
                            }catch (e:Exception){
                                e.printStackTrace()
                                progressDialog.dismiss()
                            }

                            progressDialog.dismiss()
                            // Realiza el procesamiento que necesites con estos datos
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar el error si es necesario
                    }
                })
            } else {
                // No tiene acceso a Internet
                showMessaje("No tiene acceso a Internet")
            }
        }


    }





    /* private fun SubirArchivos() {
         val lista = dataManager.DatosDelUsuario(requireContext())
         currentUser = mAuth!!.currentUser
         val dataRefe = instancias.referenciaParaLosArchivos(currentUser!!.uid)

         if (lista.isNullOrEmpty()) {
             showMessaje("No hay archivos que subir")
         } else {
             val progressDialog = ProgressDialog(requireContext())
             progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
             progressDialog.setTitle("Subiendo Archivos")
             progressDialog.max = lista.size
             progressDialog.progress = 0
             progressDialog.setCancelable(false)
             progressDialog.show()

             var filesUploadedCount = 0

             for ((index, l) in lista.withIndex()) {
                 val photoBytes = l.phtoto // Supongo que l.phtoto es un ByteArray
                 val urlString: String = "data:image/jpeg;base64," + Base64.encodeToString(photoBytes, Base64.DEFAULT)
                 val decodedBytes = Base64.decode(urlString.split(",")[1], Base64.DEFAULT)
                 val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                 val outputDir: File = requireContext().cacheDir
                 val outputFile = File.createTempFile("image", ".jpg", outputDir)
                 val outputStream = FileOutputStream(outputFile)
                 decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                 outputStream.close()

                 val storageRef = FirebaseStorage.getInstance().reference
                 val imageRef = storageRef.child("Usuarios").child(currentUser!!.uid).child("images/${currentUser!!.uid}/${l.nombreImagen}.jpg")
                 val uploadTask = imageRef.putFile(Uri.fromFile(outputFile))
                 uploadTask.addOnSuccessListener { taskSnapshot ->
                     imageRef.downloadUrl.addOnSuccessListener { uri ->
                         val imageUrl = uri.toString()

                         var u = MisImagenesFirebase(l.id, l.uid, imageUrl, l.nombreImagen, l.fechaHora)

                         dataRefe.push().setValue(u).addOnCompleteListener { task ->
                             filesUploadedCount++

                             progressDialog.progress = filesUploadedCount

                             if (filesUploadedCount == lista.size) {
                                 progressDialog.dismiss()
                                 showMessaje("Archivos Subidos")
                             }
                         }
                     }
                 }
             }
         }
     }*/

  /*  private fun SubirArchivos() {
        val lista = dataManager.misImagenes(requireContext())
        currentUser = mAuth!!.currentUser
        val dataRefe = instancias.referenciaParaLosArchivos(currentUser!!.uid)

        if (lista.isNullOrEmpty()) {
            showMessaje("No hay archivos que subir")
        } else {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.setTitle("Subiendo Archivos")
            progressDialog.max = lista.size
            progressDialog.progress = 0
            progressDialog.setCancelable(false)
            progressDialog.show()

            var filesUploadedCount = 0

            val existingImageUrls = mutableListOf<String>()

            // Consulta para obtener las URLs de las imágenes existentes
            dataRefe.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val imageUrl = childSnapshot.child("imageUrl").getValue(String::class.java)
                        imageUrl?.let { existingImageUrls.add(it) }
                    }

                    for ((index, l) in lista.withIndex()) {
                        val photoBytes = l.photo // Supongo que l.phtoto es un ByteArray
                        val urlString: String = "data:image/jpeg;base64," + Base64.encodeToString(photoBytes, Base64.DEFAULT)
                        val decodedBytes = Base64.decode(urlString.split(",")[1], Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        val imageUrl = "URL de la imagen que vas a subir"

                        if (existingImageUrls.contains(imageUrl)) {
                            // La imagen ya existe, omitir y continuar con la siguiente
                            filesUploadedCount++
                            progressDialog.progress = filesUploadedCount
                            if (filesUploadedCount == lista.size) {
                                progressDialog.dismiss()
                                showMessaje("Archivos Subidos")
                            }
                        } else {
                            // Generar un id único para la imagen
                           // val uniqueImageId = UUID.randomUUID().toString()

                            // Subir la imagen y guardar información en Firebase
                            val outputDir: File = requireContext().cacheDir
                            val outputFile = File.createTempFile("image", ".jpg", outputDir)
                            val outputStream = FileOutputStream(outputFile)
                            decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                            outputStream.close()

                            val storageRef = FirebaseStorage.getInstance().reference
                            val imageRef = storageRef.child("Usuarios").child(currentUser!!.uid).child("images/${currentUser!!.uid}/${l.nombreImagen}.jpg")
                            val uploadTask = imageRef.putFile(Uri.fromFile(outputFile))
                            uploadTask.addOnSuccessListener { taskSnapshot ->
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val imageUrl = uri.toString()

                                    val u = MisImagenesFirebase(l.id, l.uid, imageUrl, l.nombreImagen, l.fechaHora)

                                    dataRefe.child(l.id.toString()).setValue(u).addOnCompleteListener { task ->
                                        filesUploadedCount++
                                        progressDialog.progress = filesUploadedCount

                                        if (filesUploadedCount == lista.size) {
                                            progressDialog.dismiss()
                                            showMessaje("Archivos Subidos")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el error si es necesario
                }
            })
        }
    }*/

    private fun SubirArchivos() {
        mainScope.launch {
            val isConnected = withContext(Dispatchers.IO) {
                conexion.isInternetAvailable(requireContext())
            }

            if (isConnected) {
                // Tiene acceso a Internet
                val lista = dataManager.misImagenes(requireContext())
                currentUser = mAuth!!.currentUser
                val storageRef = FirebaseStorage.getInstance().reference

                if (lista.isNullOrEmpty()) {
                    showMessaje("No hay archivos que subir")
                } else {
                    val progressDialog = ProgressDialog(requireContext())
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    progressDialog.setTitle("Subiendo Archivos")
                    progressDialog.max = lista.size
                    progressDialog.progress = 0
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    var filesUploadedCount = 0

                    for ((index, l) in lista.withIndex()) {
                        val photoBytes = l.photo

                        val imageRef = storageRef.child("Usuarios").child(currentUser!!.uid).child("images/${currentUser!!.uid}/${l.nombreImagen}.jpg")
                        val uploadTask = imageRef.putBytes(photoBytes!!)

                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let { throw it }
                            }
                            imageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val imageUrl = task.result.toString()

                                // Guardar la información en Realtime Database
                                val dataRefe = instancias.referenciaParaLosArchivos(currentUser!!.uid)
                                val u = MisImagenesFirebase(l.id, l.uid, imageUrl, l.nombreImagen, l.fechaHora)
                                dataRefe.child(l.id.toString()).setValue(u).addOnCompleteListener { innerTask ->
                                    filesUploadedCount++
                                    progressDialog.progress = filesUploadedCount

                                    if (filesUploadedCount == lista.size) {
                                        progressDialog.dismiss()
                                        showMessaje("Archivos Subidos")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // No tiene acceso a Internet
                showMessaje("No tiene acceso a Internet")
            }
        }

    }






    private fun showMessaje(s: String) {
        Toast.makeText(requireContext(),s, Toast.LENGTH_SHORT).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}