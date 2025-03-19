package com.silab.data.model

import com.google.gson.annotations.SerializedName

data class GoodsResponse(

	@field:SerializedName("data")
	val data: List<GoodsDataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class GoodsDataItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("kode_barang")
	val kodeBarang: String? = null,

	@field:SerializedName("letak_barang")
	val letakBarang: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("nama_barang")
	val namaBarang: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("can_borrowed")
	val canBorrowed: Boolean? = null,

	@field:SerializedName("kondisi_barang")
	val kondisiBarang: String? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null,

	@field:SerializedName("jenis_barang")
	val jenisBarang: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
