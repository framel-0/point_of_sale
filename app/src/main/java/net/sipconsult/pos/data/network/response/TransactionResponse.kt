package net.sipconsult.pos.data.network.response

import com.google.gson.annotations.SerializedName

class TransactionResponse(
    @SerializedName("error")
    val error: String,
    @SerializedName("successful")
    val successful: Boolean
)