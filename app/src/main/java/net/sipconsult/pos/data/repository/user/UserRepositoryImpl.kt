package net.sipconsult.pos.data.repository.user

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.datasource.user.UserDataSource
import net.sipconsult.pos.data.models.LoggedInUser
import net.sipconsult.pos.data.models.SignInBody
import net.sipconsult.pos.internal.Result

class UserRepositoryImpl(private val dataSource: UserDataSource, val context: Context) :
    UserRepository {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    override fun logout() {
        setLogin(false)
//        dataSource.logout()
    }

    override suspend fun login(signInBody: SignInBody): Result<LoggedInUser> {
        // handle login

//        return withContext(Dispatchers.IO) {
        val result = dataSource.login(signInBody)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
            setLogin(true)
        }
        return result
//        }

    }

    //
    private fun setLogin(isLoggedIn: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn)

        // commit changes
        editor.apply()
//        Log.d(TAG, "User login session modified!")
    }
    override fun setLoggedInUser(loggedInUser: LoggedInUser) {
        dataSource.setLoggedInUser(loggedInUser)
    }

    override val loggedInUser: LiveData<LoggedInUser>
        get() = dataSource.loggedInUser


    override fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGEDIN, false)
    }

    companion object {
        private const val TAG: String = "LoginRepositoryImpl"
        private const val PREF_NAME = "AndroidHiveLogin"

        private const val KEY_IS_LOGGEDIN = "isLoggedIn"
    }
}