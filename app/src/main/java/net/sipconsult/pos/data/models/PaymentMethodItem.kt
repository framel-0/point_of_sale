package net.sipconsult.pos.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "payment_methods"
)
data class PaymentMethodItem(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: Int,
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String
) {
    @Ignore
    var isSelected: Boolean = false

    @Ignore
    var displayName: String = ""

    @Ignore
    var amountPaid: Double = 0.0
}