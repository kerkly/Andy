package com.example.andy.ui.gallery

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.andy.MainActivityCamara
import com.example.andy.adaptadores.AdapterImagenes
import com.example.andy.clases.MisImagenes
import com.example.andy.databinding.FragmentGalleryBinding
import com.example.andy.sqlite.DataManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class GalleryFragment : Fragment(), AdapterImagenes.OnItemClickListener {

    private var _binding: FragmentGalleryBinding? = null

    private lateinit var alertDialog: AlertDialog
    private lateinit var dataManager: DataManager
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private lateinit var editTextnombre: EditText

    //recycler
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterImagenes
    private lateinit var searchView: SearchView
   private lateinit var floatActionAgregar: FloatingActionButton

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dataManager =  DataManager(requireContext())
        mAuth = FirebaseAuth.getInstance()
        recyclerView = binding.myRecyclerG
        searchView = binding.buscarG
        floatActionAgregar = binding.actionAgregar
        adapter = AdapterImagenes(requireContext())
        adapter.itemClickListener = this
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setScrollBar()
            }
        })
        val array = dataManager.misImagenes(requireContext())
        var info: MisImagenes
        for (datos in array) {
            info = MisImagenes(datos.id,datos.uid,datos.photo,datos.nombreImagen,datos.fechaHora)
            adapter.addMensaje(info)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                recyclerView.scrollToPosition(0)
                return true
            }
        })
        // Agregar un callback para manejar el botón de regresar
             val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Aquí puedes realizar acciones antes de regresar, si es necesario
                // Por ejemplo, mostrar un diálogo de confirmación

                // Luego, realiza la acción de regresar
              /*  imageView.visibility = View.GONE
                imageView?.setImageDrawable(null)
                val linearLayout = binding.linearlayout1
                val linearLayout2 = binding.linearlayoutAgregar
                val linearLayoutB = binding.linearlayoutBuscar
                linearLayoutB.visibility = View.VISIBLE
                linearLayout.visibility = View.VISIBLE

                linearLayout2.visibility = View.GONE*/
              //  showMessaje("retroceso")
                val array = dataManager.misImagenes(requireContext())
                adapter.updateAdapterData(array)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)



        floatActionAgregar.setOnClickListener {
          // val linearLayout = binding.linearlayout1
         //   val linearLayout2 = binding.linearlayoutAgregar
          //  linearLayout.visibility = View.GONE
           // linearLayout2.visibility = View.VISIBLE
            val linearLayoutB = binding.linearlayoutBuscar
         //   linearLayoutB.visibility = View.GONE
            val intent = Intent(requireContext(),MainActivityCamara::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun setScrollBar() {
        recyclerView.scrollToPosition(adapter.itemCount-1)
    }
    override fun onItemClick(position: Int) {
        val id = adapter.lista[position].id.toLong()
        val nombre = adapter.lista[position].nombreImagen.toString()
        showOptionsDialogImagenes(id,position,nombre)
    }


    private fun showOptionsDialogImagenes(id:Long, position: Int,nombre: String) {
        val options = arrayOf("Eliminar", "Modificar")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Elija una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> Eliminar(id,position)
                1 -> Modificar(id,position,nombre)
            }
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun Modificar(id: Long,position: Int,nombre:String) {
        if (position != -1) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Modificar nombre")

            val input = EditText(requireContext())
            input.setText(nombre)
            input.textSize = 16f
            input.gravity = Gravity.START
            //input.hint = "Nuevo nombre"
            builder.setView(input)

            builder.setPositiveButton("Aceptar") { _, _ ->
                val nuevoNombre = input.text.toString()
                if (nuevoNombre.isNotEmpty()) {
                    dataManager.updateNombreById(id,nuevoNombre)
                    // adapter.modificar(position)
                    val array = dataManager.misImagenes(requireContext())
                    adapter.updateAdapterData(array)


                }
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }

    }


    private fun Eliminar(id: Long,position: Int) {
        if (position != -1) {
            dataManager.deleteRegistroById(id)
            adapter.quitar(position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*interface OnBackPressedListener {
        fun onBackPressed()
    }*/

   /* fun onBackPressed() {
        // Realiza las actualizaciones necesarias en el Fragment aquí
        val array = dataManager.misImagenes(requireContext())
        adapter.updateAdapterData(array)
showMessaje("actualizado")
    }*/


    private fun showMessaje(s: String) {
        Toast.makeText(requireContext(),s, Toast.LENGTH_SHORT).show()
    }
}
