package com.silab.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.silab.MainActivity
import com.silab.data.datastore.DataStore
import com.silab.data.model.GoodsDataItem
import com.silab.databinding.FragmentHomeBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.adapter.GoodsAdapter
import com.silab.ui.auth.login.LoginActivity
import com.silab.ui.cart.CartViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel : HomeViewModel by activityViewModels{
        ViewModelFactory.getInstance(requireContext(), requireActivity().application)
    }
    private val cartViewModel : CartViewModel by activityViewModels{
        ViewModelFactory.getInstance(requireContext(), requireActivity().application)
    }

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.getGoods()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val dataStore = DataStore.getInstance(requireActivity())

        lifecycleScope.launch {
            val token = dataStore.getToken()
            if (token.isNullOrEmpty()) {
                binding.homeLogin.visibility = View.VISIBLE
                binding.ivNotif.visibility = View.GONE
            } else  {
                val name = dataStore.getName()
                binding.tvGreeting.text = "Hello ${name}!"
                binding.homeLogin.visibility = View.GONE
                binding.ivNotif.visibility = View.VISIBLE
            }
        }

        homeViewModel.groupedGoods.observe(viewLifecycleOwner) { listGoods ->
            listGoods?.let {
                val nonNullGroupedGoods = listGoods.map { innerList ->
                    innerList.filterNotNull().toMutableList() // Remove nulls from each inner list
                }.toMutableList()

                showGoodsRecycleList(nonNullGroupedGoods, dataStore)
            }
        }

        with(binding){
            etSearch.addTextChangedListener {
                homeViewModel.updateSearchQuery(it.toString())
            }
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        return root
    }

    private fun showGoodsRecycleList(listAdapter: MutableList<MutableList<GoodsDataItem>>, dataStore: DataStore) {

        lifecycleScope.launch {
            val token = dataStore.getToken()
            if (listAdapter.isNotEmpty()) {
                binding.tvGoodsEmpty.visibility = View.GONE
                binding.rvItems.visibility = View.VISIBLE
                binding.rvItems.layoutManager = LinearLayoutManager(context)

                val goodsAdapter = GoodsAdapter(listAdapter, token)

                goodsAdapter.setOnItemClickCallback(object : GoodsAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: MutableList<GoodsDataItem>) {
                        cartViewModel.addToCart(data)
                        Log.d("ini data", "$data")
                        homeViewModel.removeGoods(data)
                    }
                })

                binding.homeLogin.setOnClickListener {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                }

                binding.rvItems.adapter = goodsAdapter

            } else {
                binding.rvItems.visibility = View.GONE
                binding.tvGoodsEmpty.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}