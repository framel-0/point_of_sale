package net.sipconsult.pos.data.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "discount_types")
data class DiscountTypesItem(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("percentage")
    val percentage: Int
) {
    val percentageStr: String
        get() {
            return String.format(
                "%s %%",
                percentage
            )
        }
}