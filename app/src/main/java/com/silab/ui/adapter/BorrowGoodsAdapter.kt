package com.silab.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.silab.ui.borrow.ActiveBorrowFragment
import com.silab.ui.borrow.CompletedBorrowingFragment
import com.silab.ui.borrow.PendingBorrowFragment

class BorrowGoodsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingBorrowFragment()
            1 -> ActiveBorrowFragment()
            2 -> CompletedBorrowingFragment()

            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}