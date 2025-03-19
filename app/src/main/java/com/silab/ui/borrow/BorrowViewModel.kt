package com.silab.ui.borrow

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silab.data.repository.Repository
import com.silab.data.model.BorrowDataItem
import kotlinx.coroutines.launch

class BorrowViewModel (private val repository: Repository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val pendingBorrowedGoods: LiveData<List<BorrowDataItem?>?> get() = _pendingBorrowedGoods
    private val _pendingBorrowedGoods = MutableLiveData<List<BorrowDataItem?>?>()

    val activeBorrowedGoods: LiveData<List<BorrowDataItem?>?> get() = _activeBorrowedGoods
    private val _activeBorrowedGoods = MutableLiveData<List<BorrowDataItem?>?>()

    val completedBorrowedGoods: LiveData<List<BorrowDataItem?>?> get() = _completedBorrowedGoods
    private val _completedBorrowedGoods = MutableLiveData<List<BorrowDataItem?>?>()

    private var isLoadedActive = false
    private val isLoadedPending = false
    private var isLoadedCompleted = false


    fun getPendingBorrow() {
        viewModelScope.launch {
            try {
                if (!isLoadedActive) _isLoading.value = true
                val goodsData = repository.getPendingBorrow()
                if (!goodsData.data.isNullOrEmpty()) {
                    _pendingBorrowedGoods.value = goodsData.data
                } else {
                    _pendingBorrowedGoods.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            } finally {
                if (!isLoadedActive) _isLoading.value = false
                isLoadedActive = true
            }
        }
    }

    fun getActiveBorrow() {
        viewModelScope.launch {
            try {
                if (!isLoadedActive) _isLoading.value = true
                val goodsData = repository.getActiveBorrow()
                if (!goodsData.data.isNullOrEmpty()) {
                    _activeBorrowedGoods.value = goodsData.data
                } else {
                    _activeBorrowedGoods.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            } finally {
                if (!isLoadedActive) _isLoading.value = false
                isLoadedActive = true
            }
        }
    }

    fun getHistoryBorrow() {
        viewModelScope.launch {
            try {
                if (!isLoadedCompleted) _isLoading.value = true
                val goodsData = repository.getHistoryBorrow()
                if (!goodsData.data.isNullOrEmpty()) {
                    val completedGoods = goodsData.data.filter { it?.status == "selesai" }
                    _completedBorrowedGoods.value = completedGoods
                } else {
                    _completedBorrowedGoods.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            } finally {
                if (!isLoadedCompleted) _isLoading.value = false
                isLoadedCompleted = true
            }
        }
    }


}