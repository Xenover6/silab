package com.silab.data.model

import com.google.gson.annotations.SerializedName

data class BorrowRequestResponse(

	@field:SerializedName("data")
	val data: BorrowRequestData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class BorrowRequestData(

	@field:SerializedName("nomor_handphone")
	val nomorHandphone: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("alamat_peminjam")
	val alamatPeminjam: String? = null,

	@field:SerializedName("tanggal_pengembalian")
	val tanggalPengembalian: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("kode_barang")
	val kodeBarang: String? = null,

	@field:SerializedName("letak_barang")
	val letakBarang: String? = null,

	@field:SerializedName("nama_barang")
	val namaBarang: String? = null,

	@field:SerializedName("nama_peminjam")
	val namaPeminjam: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("surat_tugas")
	val suratTugas: String? = null,

	@field:SerializedName("tanggal_peminjaman")
	val tanggalPeminjaman: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
