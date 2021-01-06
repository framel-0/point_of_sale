package net.sipconsult.pos.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.LoggedInUser
import net.sipconsult.pos.data.models.SignInBody
import net.sipconsult.pos.data.repository.user.UserRepository
import net.sipconsult.pos.internal.Event
import net.sipconsult.pos.internal.Result
import net.sipconsult.pos.ui.login.LoginFormState
import net.sipconsult.pos.ui.products.AuthResult

class PaymentViewModel(private val userRepository: UserRepository) : ViewModel() {


    var usernameLogin: String = ""
    var passwordLogin: String = ""

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<Event<AuthResult>>()
    val loginResult: LiveData<Event<AuthResult>> = _loginResult

    val login = liveData(Dispatchers.IO) {

        val signInBody = SignInBody(usernameLogin, passwordLogin)
        // can be launched in a separate asynchronous job
        emit(userRepository.login(signInBody))

    }

    fun updateLoginResult(result: Result<LoggedInUser>) {

        if (result is Result.Success) {
            _loginResult.value = Event(
                AuthResult(
                    success = result.data

                )
            )
        } else {
            _loginResult.value = Event(
                AuthResult(error = R.string.login_failed)
            )
        }

    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value =
                LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value =
                LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()

    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }


}
