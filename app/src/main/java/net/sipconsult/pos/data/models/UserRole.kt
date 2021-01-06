package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class UserRole(
    @SerializedName("role")
    val role: Role
)