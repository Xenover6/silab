package com.silab.ui.borrow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.silab.data.model.BorrowDataItem
import com.silab.databinding.FragmentPendingBorrowBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.adapter.BorrowAdapter

class PendingBorrowFragment : Fragment() {
    private var _binding: FragmentPendingBorrowBinding? = null
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
        _binding = FragmentPendingBorrowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        borrowViewModel.pendingBorrowedGoods.observe(viewLifecycleOwner) { goods ->
            goods?.let {
                if (it.isEmpty()) {
                    binding.tvPendingBorrowEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvPendingBorrowEmpty.visibility = View.GONE
                    showPendingBorrowRecycleList(it.map { item -> item!! }.toMutableList())
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
        borrowViewModel.getPendingBorrow()
    }


    private fun showPendingBorrowRecycleList(listAdapter: MutableList<BorrowDataItem>) {
        if (listAdapter.isNotEmpty()) {
            binding.tvPendingBorrowEmpty.visibility = View.GONE
            binding.rvPendingBorrow.visibility = View.VISIBLE
            binding.rvPendingBorrow.layoutManager = LinearLayoutManager(context)
            val borrowAdapter = BorrowAdapter(listAdapter)
            binding.rvPendingBorrow.adapter = borrowAdapter

        } else {
            binding.rvPendingBorrow.visibility = View.GONE
            binding.tvPendingBorrowEmpty.visibility = View.VISIBLE
        }
    }



}