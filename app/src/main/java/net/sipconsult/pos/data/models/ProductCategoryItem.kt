package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class ProductCategoryItem(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("ImageUrl")
    val imageUrl: String,
    @SerializedName("Name")
    val name: String
)