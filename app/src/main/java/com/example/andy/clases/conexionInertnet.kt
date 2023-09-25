package com.example.andy.clases

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class conexionInertnet {
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.google.com") // Usar un recurso en línea confiable para verificar la conectividad
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: IOException) {
            false
        }
    }
}