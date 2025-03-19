package com.silab.ui.borrow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.silab.data.model.BorrowDataItem
import com.silab.databinding.FragmentCompletedBorrowBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.adapter.BorrowAdapter

class CompletedBorrowingFragment : Fragment() {
    private var _binding: FragmentCompletedBorrowBinding? = null
    private val borrowViewModel by viewModels<BorrowViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBorrowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        borrowViewModel.getHistoryBorrow()

        borrowViewModel.completedBorrowedGoods.observe(viewLifecycleOwner) { goods ->
            goods?.let {
                if (it.isEmpty()) {
                    binding.tvCompletedBorrowEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvCompletedBorrowEmpty.visibility = View.GONE
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
        borrowViewModel.getHistoryBorrow()
    }


    private fun showPendingBorrowRecycleList(listAdapter: MutableList<BorrowDataItem>) {
        if (listAdapter.isNotEmpty()) {
            binding.tvCompletedBorrowEmpty.visibility = View.GONE
            binding.rvCompletedBorrow.visibility = View.VISIBLE
            binding.rvCompletedBorrow.layoutManager = LinearLayoutManager(context)
            val borrowAdapter = BorrowAdapter(listAdapter)
            binding.rvCompletedBorrow.adapter = borrowAdapter

        } else {
            binding.rvCompletedBorrow.visibility = View.GONE
            binding.tvCompletedBorrowEmpty.visibility = View.VISIBLE
        }
    }
}