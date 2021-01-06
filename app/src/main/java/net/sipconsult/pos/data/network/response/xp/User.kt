package net.sipconsult.pos.data.network.response.xp


import com.google.gson.annotations.SerializedName
import net.sipconsult.pos.data.models.UserRole

data class User(
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
    val userRoles: List<UserRole>
)