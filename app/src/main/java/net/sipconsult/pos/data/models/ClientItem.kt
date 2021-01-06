package net.sipconsult.pos.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "clients"
)
data class ClientItem(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,

    @SerializedName("code")
    val code: String,

    @SerializedName("firstName")
    @ColumnInfo(name = "first_name")
    val firstName: String,

    @SerializedName("lastName")
    @ColumnInfo(name = "last_name")
    val lastName: String,

    @SerializedName("fullName")
    @ColumnInfo(name = "full_name")
    val fullName: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("phoneNumber1")
    @ColumnInfo(name = "phone_number_one")
    val phoneNumber1: String,

    @SerializedName("phoneNumber2")
    @ColumnInfo(name = "phone_number_two")
    val phoneNumber2: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("description")
    val description: String
)