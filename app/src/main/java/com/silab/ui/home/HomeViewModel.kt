package com.silab.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silab.data.repository.Repository
import com.silab.data.model.GoodsDataItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class HomeViewModel (private val repository: Repository, application: Application) : AndroidViewModel(application)   {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow<String?>(null) // Untuk menyimpan query

    val groupedGoods: LiveData<MutableList<MutableList<GoodsDataItem?>>> get() = _groupedGoods
    private val _groupedGoods = MutableLiveData<MutableList<MutableList<GoodsDataItem?>>>()

    private var originalGroupedGoods: MutableList<MutableList<GoodsDataItem?>> = mutableListOf()


    // Menggunakan query untuk filter data
    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    query?.let {
                        if (it.isEmpty()) {
                            // Jika query kosong, kembalikan ke data asli
                            _groupedGoods.value = originalGroupedGoods
                        } else {
                            // Filter data berdasarkan query
                            val filteredGroupedGoods = originalGroupedGoods.filter { listGoodsItem ->
                                listGoodsItem.any { goodsItem ->
                                    goodsItem?.namaBarang?.contains(query, ignoreCase = true) == true
                                }
                            }
                            _groupedGoods.value = filteredGroupedGoods.toMutableList()
                        }
                    }
                }
        }
    }

    fun removeGoods(listGoods: MutableList<GoodsDataItem>) {

        viewModelScope.launch {
            listGoods.forEach { item ->
                originalGroupedGoods.forEach { group ->
                    group.remove(item)
                }
                originalGroupedGoods.removeIf { it.isEmpty()}
            }
            _groupedGoods.postValue(originalGroupedGoods)
        }

    }


    fun addGoods(goods: GoodsDataItem) {
        var isAdded = false

        originalGroupedGoods.forEach { listGoods ->
            if (listGoods.isNotEmpty() && listGoods[0]?.namaBarang == goods.namaBarang) {
                listGoods.add(goods)
                isAdded = true
                return@forEach
            }
        }

        if (!isAdded) {
            originalGroupedGoods.add(mutableListOf(goods))
        }
        _groupedGoods.value = originalGroupedGoods
    }


    private fun groupGoodsByName(goodsList: MutableList<GoodsDataItem?>): MutableList<MutableList<GoodsDataItem?>> {
        return goodsList.groupBy { it?.namaBarang }
            .map { it.value.toMutableList() }
            .toMutableList()
    }

    fun updateSearchQuery(query: String?) {
        _searchQuery.value = query // Update query
    }

     fun getGoods(query: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val goodsData = repository.getGoods(query)
                if (!goodsData.data.isNullOrEmpty()) {
                    _groupedGoods.value = groupGoodsByName(goodsData.data.toMutableList())
                    originalGroupedGoods = groupGoodsByName(goodsData.data.toMutableList())
                } else {
                    _groupedGoods.value = mutableListOf()
                }
            } catch (e: Exception) {
                Log.d("ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }
}