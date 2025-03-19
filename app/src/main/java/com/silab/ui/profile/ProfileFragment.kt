package com.silab.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.silab.R
import com.silab.data.datastore.DataStore
import com.silab.databinding.FragmentProfileBinding
import com.silab.ui.auth.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupToolbar()
        setupProfileData()
        setupLogoutButton()

        return root
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbarProfile.toolbar
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)

        binding.toolbarProfile.toolbarTitle.text = getString(R.string.profil)
        binding.toolbarProfile.toolbar.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.purple_800)
        )
        binding.toolbarProfile.toolbarTitle.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.white)
        )
    }

    private fun setupProfileData() {
        val dataStore = DataStore.getInstance(requireActivity())

        lifecycleScope.launch(Dispatchers.IO) {
            val userName = dataStore.getName()
            val email = dataStore.getEmail()

            launch(Dispatchers.Main) {
                binding.tvUserName.text = userName
                binding.tvEmailUser.text = email
            }
        }
    }

    private fun setupLogoutButton() {
        val dataStore = DataStore.getInstance(requireActivity())
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                dataStore.clear()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
