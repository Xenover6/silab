package com.silab.ui.borrow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.silab.R
import com.silab.databinding.FragmentBorrowBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.adapter.BorrowGoodsAdapter

class BorrowFragment : Fragment() {

    private var _binding: FragmentBorrowBinding? = null
    private val borrowViewModel by viewModels<BorrowViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBorrowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar: Toolbar = binding.toolbarBorrow.toolbar
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        binding.toolbarBorrow.toolbarTitle.text = getString(R.string.daftar_barang_pinjaman)

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val adapter = BorrowGoodsAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Pending"
                1 -> tab.text = "Aktif"
                2 -> tab.text = "Selesai"
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}