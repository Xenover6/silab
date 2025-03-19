package com.silab.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.silab.data.repository.Repository
import com.silab.ui.auth.AuthViewModel
import com.silab.ui.borrow.BorrowViewModel
import com.silab.ui.cart.CartViewModel
import com.silab.ui.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (private val repository: Repository, private val application: Application? = null,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return application?.let {
                HomeViewModel(repository, it) as T
            } ?: throw IllegalArgumentException("Application is required for CartViewModel")
        } else if (modelClass.isAssignableFrom(BorrowViewModel::class.java)) {
            return BorrowViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return application?.let {
                CartViewModel(repository, it) as T
            } ?: throw IllegalArgumentException("Application is required for CartViewModel")
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, application: Application? = null): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    application ?: (context.applicationContext as? Application)
                    ?: throw IllegalArgumentException("Application is required")
                ).also { INSTANCE = it }
            }
        }
    }

}