package com.silab.data.api

import com.silab.data.model.BorrowRequestResponse
import com.silab.data.model.BorrowResponse
import com.silab.data.model.GoodsResponse
import com.silab.data.model.LoginResponse
import com.silab.data.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ) : LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        ) : RegisterResponse

    @GET("barang")
    suspend fun getGoods(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null
    ): GoodsResponse

    @GET("peminjaman")
    suspend fun getPendingBorrow(
        @Header("Authorization") token: String,
    ): BorrowResponse

    @GET("peminjaman/aktif")
    suspend fun getActiveBorrow(
        @Header("Authorization") token: String,
    ): BorrowResponse

    @GET("peminjaman/riwayat")
    suspend fun getHistoryBorrow(
        @Header("Authorization") token: String,
    ): BorrowResponse

    @Multipart
    @POST("peminjaman")
    suspend fun postBorrowRequest(
        @Header("Authorization") token: String,
        @Part("kode_barang") kodeBarang: RequestBody,
        @Part("nama_peminjam") namaPeminjam: RequestBody,
        @Part("alamat_peminjam") alamatPeminjam: RequestBody,
        @Part("nomor_handphone") nomorHandphone: RequestBody,
        @Part("tanggal_peminjaman") tanggalPeminjaman: RequestBody,
        @Part("tanggal_pengembalian") tanggalPengembalian: RequestBody,
        @Part file: MultipartBody.Part
    ): BorrowRequestResponse

}