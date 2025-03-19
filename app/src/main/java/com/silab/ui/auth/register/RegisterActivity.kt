package com.silab.ui.auth.register

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
import com.silab.databinding.ActivityRegisterBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.auth.AuthViewModel
import com.silab.ui.auth.login.LoginActivity
import com.silab.utils.LoadingDialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RegisterActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(applicationContext)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val loadingDialog = LoadingDialog(this)
        val view = findViewById<View>(android.R.id.content)

        // Mengamati perubahan status loading
        authViewModel.isLoading.observe(this) { isLoading ->
            // Tampilkan progress bar jika loading
            if (isLoading) loadingDialog.startLoadingDialog() else loadingDialog.dismissDialog()
        }

        // Mengamati error register
        authViewModel.error.observe(this) {
            Snackbar.make(view, "Failed to Register", Snackbar.LENGTH_LONG)
                .show()
        }

        // Mengamati apakah perlu menavigasi ke login
        authViewModel.navigateToLogin.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                lifecycleScope.launch {
                    showAlertDialogAwait("Sukses", "Anda berhasil register")
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Menangani tombol register
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val passwordConfirmation = binding.etConfirmPassword.text.toString()
            if (name.isEmpty() || name == "") {
                Snackbar.make(view, "Nama tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show()
            } else if (email.isEmpty() || email == "" ) {
                Snackbar.make(view, "Email tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show()
            } else if (password.isEmpty() ||  password == "" ) {
                Snackbar.make(view, "Password tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show()
            } else if (passwordConfirmation != password) {
                Snackbar.make(view, "Konfirmasi password tidak sama", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                authViewModel.register(name,email, password, passwordConfirmation)
            }
        }
    }

    private suspend fun showAlertDialogAwait(title: String, subtitle: String) {
        suspendCancellableCoroutine { continuation ->
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