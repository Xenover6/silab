package com.silab.data.repository

import com.silab.data.api.ApiConfig
import com.silab.data.datastore.DataStore
import com.silab.data.model.BorrowRequestResponse
import com.silab.data.model.BorrowResponse
import com.silab.data.model.GoodsResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class Repository(private val dataStore: DataStore){

    suspend fun login(email: String, password: String): Boolean {
        val response = ApiConfig.getApiService().login(email, password)

        if (response.success) {
            dataStore.saveToken(response.data.token)
            dataStore.saveName(response.data.user.name)
            dataStore.saveEmail(response.data.user.email)
        }
        return response.success
    }

    suspend fun register(name:String, email: String, password: String, passwordConfirmation: String): Boolean {
        val response = ApiConfig.getApiService().register(name, email, password, passwordConfirmation)

        return response.success
    }

    suspend fun getGoods(query : String? = null): GoodsResponse {
        val token = dataStore.getToken()
        val response = ApiConfig.getApiService().getGoods("Bearer $token", query)

        return response
    }

    suspend fun getPendingBorrow(): BorrowResponse {
        val token = dataStore.getToken()
        val response = ApiConfig.getApiService().getPendingBorrow("Bearer $token")

        return response
    }

    suspend fun getActiveBorrow(): BorrowResponse {
        val token = dataStore.getToken()
        val response = ApiConfig.getApiService().getActiveBorrow("Bearer $token")

        return response
    }

    suspend fun getHistoryBorrow(): BorrowResponse {
        val token = dataStore.getToken()
        val response = ApiConfig.getApiService().getHistoryBorrow("Bearer $token")

        return response
    }

    suspend fun submitBorrowRequest(kodeBarang : String, namaPeminjam: String, alamatPeminjam: String, nomorHandphone: String, tanggalPeminjaman: String, tanggalPengembalian:String, suratTugasFile: MultipartBody.Part): BorrowRequestResponse {
        val token = dataStore.getToken()
        val kodeBarangRB = kodeBarang.toRequestBody("text/plain".toMediaTypeOrNull())
        val namaPeminjamRB = namaPeminjam.toRequestBody("text/plain".toMediaTypeOrNull())
        val alamatPeminjamRB = alamatPeminjam.toRequestBody("text/plain".toMediaTypeOrNull())
        val nomorHandphoneRB =  nomorHandphone.toRequestBody("text/plain".toMediaTypeOrNull())
        val tanggalPeminjamanRB = tanggalPeminjaman.toRequestBody("text/plain".toMediaTypeOrNull())
        val tanggalPengembalianRB = tanggalPengembalian.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = ApiConfig.getApiService().postBorrowRequest(
            "Bearer $token",
            kodeBarang = kodeBarangRB,
            namaPeminjam = namaPeminjamRB,
            alamatPeminjam = alamatPeminjamRB,
            nomorHandphone = nomorHandphoneRB,
            tanggalPeminjaman = tanggalPeminjamanRB,
            tanggalPengembalian = tanggalPengembalianRB,
            file = suratTugasFile
        )
        return response
    }

    companion object {
        @Volatile
        private var instances: Repository? = null

        fun getInstance(
            dataStore: DataStore
        ): Repository = instances ?: synchronized(this) {
            instances ?: Repository(dataStore)
                .also { instances = it }
        }
    }
}

