package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class Operator(
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userRoles")
    val userRoles: List<UserRole>?
)