package com.example.andy.adaptadores

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.andy.R
import com.example.andy.clases.MisImagenes
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.Normalizer
import java.util.Locale


class AdapterImagenes(c: Context): RecyclerView.Adapter<AdapterImagenes.ViewHolder>()  {
    var context = c
     val lista = ArrayList<MisImagenes>()
    private val originalList = ArrayList<MisImagenes>()
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    // ... (otros métodos y propiedades)



    init {
        originalList.addAll(lista)
    }

    fun filter(query: String) {
        lista.clear()
        println("tamaño ${originalList.size}")
        if (query.isEmpty()) {
            lista.addAll(originalList)
        } else {
            val lowerQuery = removeAccents(query).toLowerCase(Locale.getDefault())
            originalList.forEach { item ->
                val lowerItemNombre = removeAccents(item.nombreImagen).toLowerCase(Locale.getDefault())
                if (lowerItemNombre.contains(lowerQuery)) {
                    lista.add(0, item)
                    println("lista adaptador $item")
                } else {
                    lista.add(item)
                }
            }
        }

        notifyDataSetChanged()
    }

    private fun removeAccents(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return normalized.replace("\\p{M}".toRegex(), "")
    }


    /* fun filter(query: String) {
        lista.clear()
         println("tamaño ${originalList.size}")
         if (query.isEmpty()) {
             lista.addAll(originalList)
         } else {
             val lowerQuery = query.toLowerCase(Locale.getDefault())
             originalList.forEach { item ->
                 val lowerItemNombre = item.nombreImagen.toLowerCase(Locale.getDefault())
                 if (lowerItemNombre.contains(lowerQuery)) {
                     lista.add(0, item)
                     println("lista adaptador $item")
                 } else {
                     lista.add(item)
                 }
             }
         }

         notifyDataSetChanged()
     }*/
  /*fun filter(query: String) {
      lista.clear()
    //  println("tamaño ${originalList.size}")

      if (query.isEmpty()) {
          lista.addAll(originalList)
      } else {
          val lowerQuery = query.toLowerCase(Locale.getDefault())
          val matchingItems = mutableListOf<MisImagenes>()
          val nonMatchingItems = mutableListOf<MisImagenes>()

          originalList.forEach { item ->
              val lowerItemNombre = item.nombreImagen.toLowerCase(Locale.getDefault())
              if (lowerItemNombre.contains(lowerQuery)) {
                  matchingItems.add(item)
              } else {
                  nonMatchingItems.add(item)
              }
          }

          lista.addAll(nonMatchingItems)
          lista.addAll(matchingItems)
      }

      notifyDataSetChanged()
  }*/



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
            }
        }
       val imagenes = view.findViewById<ImageView>(R.id.imageView)
        val txt_fecha = view.findViewById<TextView>(R.id.FechaHora)
        var nombre = view.findViewById<TextView>(R.id.nombre)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_datos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_fecha.text = lista[position].fechaHora
        holder.nombre.text = lista[position].nombreImagen
        val photoUrl = lista[position].photo
        val urlString: String = "data:image/jpeg;base64," + Base64.encodeToString(photoUrl, Base64.DEFAULT)
        val decodedBytes = Base64.decode(urlString.split(",")[1], Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        holder.imagenes.setImageBitmap(decodedBitmap)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
    fun addMensaje(m: MisImagenes) {
        lista.add(m)
        originalList.add(m)
        notifyItemInserted(lista.size)
    }

    fun quitar(position: Int) {
            lista.removeAt(position)
            notifyItemRemoved(position)

    }
    fun modificar(position: Int) {
        notifyItemChanged(position)

    }
    fun updateAdapterData(newData: List<MisImagenes>) {
        lista.clear()
        originalList.clear()
        lista.addAll(newData)
        originalList.addAll(newData)
        notifyDataSetChanged()
    }

}