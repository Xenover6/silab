package com.silab.data.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("RegisterData")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean
)

data class RegisterData(

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("token")
	val token: String? = null
)