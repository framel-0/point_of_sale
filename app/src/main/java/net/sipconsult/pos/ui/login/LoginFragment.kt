package net.sipconsult.pos.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*
import net.sipconsult.pos.MainActivity
import net.sipconsult.pos.R
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.ui.login.AuthenticationState.AUTHENTICATED
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class LoginFragment : Fragment(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var sharedViewModel: SharedViewModel

    private val viewModelFactory: LoginViewModelFactory by instance()
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        val navController = findNavController(this)
        sharedViewModel.authenticationState.observe(
            viewLifecycleOwner,
            Observer { authenticationState ->
                when (authenticationState) {
                    AUTHENTICATED -> navController.navigate(R.id.nav_home)
//                AuthenticationState.UNAUTHENTICATED -> navController.navigate(R.id.login_fragment)
                }
            })

        bindUI()
    }

    private fun bindUI() {
//        val usernameEditText = view.findViewById<EditText>(R.id.username)
//        val passwordEditText = view.findViewById<EditText>(R.id.password)
//        val loginButton = view.findViewById<Button>(R.id.login)
//        loadingProgressBar = view.findViewById<ProgressBar>(R.id.loading)

//        username.setText("Admin")
//        password.setText("Admin@1234")


        viewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                login.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    username.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    password.error = getString(it)
                }
            })



        viewModel.loginResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                loading.visibility = View.GONE

                result.getContentIfNotHandled()?.let { loginResult ->
                    loginResult.error?.let {
                        showLoginFailed(it)
                        sharedViewModel.logout()
                    }
                    loginResult.success?.let {
                        sharedViewModel.authenticate()
                        activity?.finish()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        updateUiWithUser(it)
                    }
                }
            })


        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }
        }
        username.addTextChangedListener(afterTextChangedListener)
        password.addTextChangedListener(afterTextChangedListener)
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.usernameLogin = username.text.toString()
                viewModel.passwordLogin = password.text.toString()
//                ldIn()

            }
            false
        }

        login.setOnClickListener {

            viewModel.usernameLogin = username.text.toString()
            viewModel.passwordLogin = password.text.toString()
            ldIn()
            loading.visibility = View.VISIBLE


        }
    }

    private fun ldIn() {
        val result = viewModel.login
        result.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            it.let {
                viewModel.updateLoginResult(it)
            }
        })
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + " " + model.displayName
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
        activity?.finish()
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}