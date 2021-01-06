package net.sipconsult.pos.data.models


import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("costPrice")
    @ColumnInfo(name = "cost_price")
    val costPrice: Double?,
    @SerializedName("salePrice")
    @ColumnInfo(name = "sale_price")
    val salePrice: Double
)