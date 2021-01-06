package net.sipconsult.pos.data.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sales_agent")
data class SalesAgentsItem(
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @SerializedName("lastName")
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @SerializedName("phoneNumber")
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @SerializedName("userName")
    @ColumnInfo(name = "user_name")
    val userName: String
) {
    @SerializedName("userRoles")
    @Ignore
    val userRoles: List<UserRole>? = null

    val displayName: String
        get() = String.format("%s %s", firstName, lastName)
}