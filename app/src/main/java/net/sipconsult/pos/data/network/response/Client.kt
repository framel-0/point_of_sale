package net.sipconsult.pos.data.network.response


import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("address")
    val address: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("companyName")
    val companyName: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber1")
    val phoneNumber1: String,
    @SerializedName("phoneNumber2")
    val phoneNumber2: String
)