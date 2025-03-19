package com.silab.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.silab.MainActivity
import com.silab.R
import com.silab.databinding.ActivityLoginBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.auth.AuthViewModel
import com.silab.ui.auth.register.RegisterActivity
import com.silab.utils.LoadingDialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(applicationContext)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val loadingDialog = LoadingDialog(this)
        val view = findViewById<View>(android.R.id.content)

        // Mengamati perubahan status loading
        authViewModel.isLoading.observe(this) { isLoading ->
            // Tampilkan progress bar jika loading
            if (isLoading) loadingDialog.startLoadingDialog() else loadingDialog.dismissDialog()
        }

        // Mengamati error login
        authViewModel.error.observe(this) {
            Snackbar.make(view, "Failed to login", Snackbar.LENGTH_LONG)
                .show()
        }

        // Mengamati apakah perlu menavigasi ke Home
        authViewModel.navigateToHome.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                lifecycleScope.launch {
                    showAlertDialogAwait("Sukses", "Anda berhasil login")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Menangani tombol login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() || email == "" ) {
                Snackbar.make(view, "Email tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show()
            } else if (password.isEmpty() ||  password == "" ) {
                Snackbar.make(view, "Password tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                authViewModel.login(email, password)

            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private suspend fun showAlertDialogAwait(title: String, subtitle: String) {
        suspendCancellableCoroutine<Unit> { continuation ->
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(subtitle)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                continuation.resume(Unit)
            }
            val dialog = builder.create()
            dialog.setOnCancelListener {
                continuation.resume(Unit)
            }
            dialog.show()
        }
    }
}