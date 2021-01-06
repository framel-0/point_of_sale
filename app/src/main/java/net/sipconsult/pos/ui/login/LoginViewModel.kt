package net.sipconsult.pos.ui.login

import android.util.Patterns
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


class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState

    var usernameLogin: String = ""
    var passwordLogin: String = ""

    init {
        if (userRepository.isLoggedIn())
        // In this example, the user is always unauthenticated when MainActivity is launched
            _authenticationState.value = AuthenticationState.AUTHENTICATED
        else
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED

    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<Event<LoginResult>>()
    val loginResult: LiveData<Event<LoginResult>> = _loginResult

    val login = liveData(Dispatchers.IO) {

        val signInBody = SignInBody(usernameLogin, passwordLogin)
        // can be launched in a separate asynchronous job
        emit(userRepository.login(signInBody))

    }

    fun logout() {
        userRepository.logout()
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun updateLoginResult(result: Result<LoggedInUser>) {

        if (result is Result.Success) {
            _loginResult.value = Event(
                LoginResult(
                    success = LoggedInUserView(
                        displayName = result.data.displayName
                    )
                )
            )
            _authenticationState.value = AuthenticationState.AUTHENTICATED
        } else {
            _loginResult.value = Event(
                LoginResult(error = R.string.login_failed)
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
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun authenticate() {
        _authenticationState.value = AuthenticationState.AUTHENTICATED
    }

}