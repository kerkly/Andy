package com.example.andy.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.andy.data.LoginDataSource
import com.example.andy.data.LoginRepository
import com.example.andy.sqlite.DataManager

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(private val dataManager: DataManager) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(dataManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
