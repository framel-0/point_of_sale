package net.sipconsult.pos.data.models

import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

class SProductItem(
    @SerializedName("barcode")
    val barcode: String?,

    @SerializedName("description")
    val description: String,

    @SerializedName("code")
    @PrimaryKey(autoGenerate = false)
    val code: String,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("salePrice")
    @Embedded
    val salePrice: Double,

    @SerializedName("quantity")
    val quantity: Int
) {
    fun displaySalesPrice(): String? {
        return String.format("GHC %.2f ", salePrice)

    }
}