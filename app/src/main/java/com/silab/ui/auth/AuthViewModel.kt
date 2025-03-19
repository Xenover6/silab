package com.silab.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silab.data.repository.Repository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: Repository) : ViewModel()  {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _navigateToHome.value = false
            try {
                _isLoading.value = true
                val response = repository.login(email, password)

                _navigateToHome.value = response
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.message}"
                Log.e("LoginViewModel", "Login failed: ${e.message}")
            }  finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name:String, email: String, password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            _navigateToLogin.value = false
            try {
                _isLoading.value = true
                val response = repository.register(name, email, password, passwordConfirmation)

                _navigateToLogin.value = response
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.message}"
                Log.e("AuthViewModel", "Register failed: ${e.message}")
            }  finally {
                _isLoading.value = false
            }
        }
    }

}