package com.silab.ui.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silab.data.repository.Repository
import com.silab.data.model.GoodsDataItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CartViewModel (private val repository: Repository, application: Application) : AndroidViewModel(application) {

    private val _listCartItem = MutableLiveData<MutableList<GoodsDataItem?>>()
    val listCartItem: LiveData<MutableList<GoodsDataItem?>> get() = _listCartItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _navigateToBorrow = MutableLiveData<Boolean>()
    val navigateToBorrow: LiveData<Boolean> = _navigateToBorrow

    init {
        _listCartItem.value = mutableListOf()
    }

    fun addToCart(listGoods: MutableList<GoodsDataItem>) {
        val currentList = _listCartItem.value ?: mutableListOf()
        listGoods.forEach {
            currentList.add(it)
        }
        _listCartItem.postValue(currentList)

    }

    fun removeFromCart(goods: GoodsDataItem) {
        val currentList = _listCartItem.value ?: mutableListOf()
        currentList.remove(goods)
        _listCartItem.postValue(currentList)
    }

    private fun removeAllFromCart() {
        _listCartItem.value = mutableListOf()
    }

    fun postBorrowRequest(
        namaPeminjam: String,
        alamatPeminjam: String,
        nomorHandphone: String,
        tanggalPeminjaman: String,
        tanggalPengembalian: String,
        suratTugasFile: MultipartBody.Part
    ) {

        // Ensure listCartItem is observed only once and not repeatedly
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val deferredResponses = listCartItem.value?.map { item ->
                    async {
                        try {
                            repository.submitBorrowRequest(
                                item?.kodeBarang!!,
                                namaPeminjam,
                                alamatPeminjam,
                                nomorHandphone,
                                tanggalPeminjaman,
                                tanggalPengembalian,
                                suratTugasFile
                            )
                        } catch (e: Exception) {
                            Log.d("ERROR", e.message.toString())
                            _error.value = "Failed to post borrowing goods for item ${item?.kodeBarang}"
                        }
                    }
                }
                deferredResponses?.awaitAll()

                removeAllFromCart()
                _navigateToBorrow.value = true
            } catch (e: Exception) {
                _error.value = "Failed to post borrowing goods"
            } finally {
                _isLoading.value = false
            }
        }

    }
}