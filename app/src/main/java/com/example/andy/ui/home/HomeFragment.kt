package com.example.andy.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView;
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.andy.adaptadores.AdapterImagenes
import com.example.andy.clases.MisImagenes
import com.example.andy.databinding.FragmentHomeBinding
import com.example.andy.sqlite.DataManager

class HomeFragment : Fragment(), AdapterImagenes.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterImagenes
    private lateinit var dataBD: DataManager
    private lateinit var searchView: SearchView
    private lateinit var imageView: ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.myRecycler
        searchView = binding.buscar

        imageView = binding.imageView
        dataBD = DataManager(requireContext())
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
        val array = dataBD.misImagenes(requireContext())
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
                println("aquiii $newText")
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
                imageView.visibility = View.GONE
                imageView?.setImageDrawable(null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)


        return root
    }
    private fun setScrollBar() {
        recyclerView.scrollToPosition(adapter.itemCount-1)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        val selectedImage = adapter.lista[position].photo
        //showMessaje(selectedImage)
        imageView.visibility = View.VISIBLE
        cargarImagen(selectedImage!!)
    }

    private fun cargarImagen(photoUrl: ByteArray) {
        val urlString: String = "data:image/jpeg;base64," + Base64.encodeToString(photoUrl, Base64.DEFAULT)
        val decodedBytes = Base64.decode(urlString.split(",")[1], Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(decodedBitmap)
    }


    private fun showMessaje(s: String) {
        Toast.makeText(requireContext(),s, Toast.LENGTH_SHORT).show()
    }

}