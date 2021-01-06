package net.sipconsult.pos.data.network.response


import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("code")
    @ColumnInfo(name = "category_code")
    val code: String?,
    @SerializedName("description")
    @ColumnInfo(name = "category_description")
    val description: String?
)