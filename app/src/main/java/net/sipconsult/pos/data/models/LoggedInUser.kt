package net.sipconsult.pos.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*


const val CURRENT_USER_ID = 0

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Entity(tableName = "users")
data class LoggedInUser(
    @ColumnInfo(name = "user_id")
    @SerializedName("id")
    val id: String,
    @SerializedName("userName")
    @ColumnInfo(name = "username")
    val userName: String,
    @ColumnInfo(name = "first_Name")
    @SerializedName("firstName")
    val firstName: String,
    @ColumnInfo(name = "last_Name")
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("email")
    val email: String

) {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var userId: Int = CURRENT_USER_ID

    @Ignore
    @SerializedName("userRoles")
    val userRoles: List<UserRole> = listOf()

    val displayName: String
        get() = String.format("%s %s", firstName, lastName)

    val abbrv: String
        get() {
            val first: String = firstName.substring(0, 1).toUpperCase(Locale.getDefault())
            val last: String = lastName.substring(0, 1).toUpperCase(Locale.getDefault())
            return String.format("%s%s", first, last)
        }
}