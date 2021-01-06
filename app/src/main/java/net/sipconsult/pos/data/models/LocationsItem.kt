package net.sipconsult.pos.data.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "locations"
)
data class LocationsItem(
    @SerializedName("code")
    @PrimaryKey(autoGenerate = false)
    val code: String = "",
    @SerializedName("mobileNumber1")
    @ColumnInfo(name = "mobile_number_one")
    val mobileNumber1: String? = "",
    @SerializedName("mobileNumber2")
    @ColumnInfo(name = "mobile_number_two")
    val mobileNumber2: String? = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("address")
    val address: String? = "",
    @SerializedName("telephone")
    val telephone: String? = ""
)