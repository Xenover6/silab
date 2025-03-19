package com.silab.ui.borrow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.silab.data.model.BorrowDataItem
import com.silab.databinding.FragmentActiveBorrowBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.adapter.BorrowAdapter


class ActiveBorrowFragment : Fragment() {
    private var _binding: FragmentActiveBorrowBinding? = null
    private val borrowViewModel by viewModels<BorrowViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveBorrowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        borrowViewModel.activeBorrowedGoods.observe(viewLifecycleOwner) { goods ->
            goods?.let {
                if (it.isEmpty()) {
                    binding.tvActiveBorrowEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvActiveBorrowEmpty.visibility = View.GONE
                    showActiveActiveRecycleList(it.map { item -> item!! }.toMutableList())
                }
            }
        }

        borrowViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        borrowViewModel.getActiveBorrow()
    }

    private fun showActiveActiveRecycleList(listAdapter: MutableList<BorrowDataItem>) {
        if (listAdapter.isNotEmpty()) {
            binding.rvActiveBorrow.visibility = View.VISIBLE
            binding.rvActiveBorrow.layoutManager = LinearLayoutManager(context)
            val borrowAdapter = BorrowAdapter(listAdapter)
            binding.rvActiveBorrow.adapter = borrowAdapter

        } else {
            binding.rvActiveBorrow.visibility = View.GONE
            binding.tvActiveBorrowEmpty.visibility = View.VISIBLE
        }
    }
}