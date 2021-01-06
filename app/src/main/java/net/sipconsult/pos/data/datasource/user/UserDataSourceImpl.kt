package net.sipconsult.pos.data.datasource.user

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.db.UserDao
import net.sipconsult.pos.data.models.LoggedInUser
import net.sipconsult.pos.data.models.SignInBody
import net.sipconsult.pos.data.network.ShopApiService
import net.sipconsult.pos.internal.Result
import java.io.IOException

class UserDataSourceImpl(
    private val shopApiService: ShopApiService,
    private val userDao: UserDao
) : UserDataSource {
    override suspend fun login(signInBody: SignInBody): Result<LoggedInUser> {

        try {

            val loginResponse = shopApiService.authenticateAsync(signInBody)

            return if (loginResponse.successful) {
                Result.Success(
                    loginResponse.user
                )

            } else {
                Result.Error(
                    IOException("Error logging in")
                )
            }

        } catch (e: Throwable) {
            return Result.Error(
                IOException("Error logging in", e)
            )
        }
    }

    override fun logout() {
        userDao.delete()
    }

    override fun setLoggedInUser(loggedInUser: LoggedInUser) {
        userDao.upsert(loggedInUser)
    }

    override val loggedInUser: LiveData<LoggedInUser>
        get() = userDao.getUser()


}