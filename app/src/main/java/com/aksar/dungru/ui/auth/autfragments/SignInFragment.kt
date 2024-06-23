package com.aksar.dungru.ui.auth.autfragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentLoginBinding
import com.aksar.dungru.models.request.OtpReq
import com.aksar.dungru.models.request.SignInReq
import com.aksar.dungru.models.request.SocialLogInReq
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.home.HomeActivity
import com.aksar.dungru.utils.Constance.FACEBOOK
import com.aksar.dungru.utils.Constance.ForgotPassword
import com.aksar.dungru.utils.Constance.GOOGLE
import com.aksar.dungru.utils.Constance.Otp
import com.aksar.dungru.utils.Constance.SignIn
import com.aksar.dungru.utils.Constance.SignUp
import com.aksar.dungru.utils.Constance.WebClientId
import com.aksar.dungru.utils.NetworkUtils.IS_CONNECTED
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.APP_STATE
import com.aksar.dungru.utils.PrefManager.Companion.TEMP_EMAIL
import com.aksar.dungru.utils.PrefManager.Companion.USER_DATA
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.hideKeyboard
import com.aksar.dungru.utils.extensions.hideProgressDialog
import com.aksar.dungru.utils.extensions.isAllFieldAreValidated
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.setPasswordVisible
import com.aksar.dungru.utils.extensions.showProgressDialog
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.AuthViewModel
import com.aksar.dungru.viewModel.OtpViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var otpViewModel: OtpViewModel
    private val GOOGLE_SIGN_IN_REQ = 111
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var callbackManager: CallbackManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        themeColorControl()
        setPasswordVisible(requireContext(), binding.etPassword)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[AuthViewModel::class.java]

        otpViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[OtpViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(requireContext())
        callbackManager = create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WebClientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        initObserver()

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.etEmail.error = null
                } else {
                    binding.etEmail.error = "Invalid email!"
                }
            }
        })
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 8)
                    binding.etPassword.error = "Password must be at least 8 characters"
                else
                    binding.etPassword.error = null
            }
        })

        binding.btnDontHaveAcc.setOnClickListener {
            replaceFragment(R.id.authContainer, SignUpFragment(), false, SignUp)
        }

        binding.btnForgotPassword.setOnClickListener {
            replaceFragment(R.id.authContainer, ForgotPasswordFragment(), true, ForgotPassword)
        }

        binding.btnSignIn.setOnClickListener {
            it.hideKeyboard()
            PrefManager.get().removeKey(APP_STATE)
            if (isAllFieldAreValidated(binding.etEmail, binding.etPassword)) {
                PrefManager.get().save(TEMP_EMAIL, binding.etEmail.text.toString().trim())
                viewModel.fetchUser(
                    SignInReq(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    )
                )
            }
        }

        binding.btnGoogle.setOnClickListener {
            if (IS_CONNECTED) googleAuthentication()
        }

        binding.btnFacebook.setOnClickListener {
            if (IS_CONNECTED) facebookAuthentication()
        }

        return binding.root
    }
    private fun googleAuthentication() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQ)
        showProgressDialog(R.raw.loading_animation_livepage)
    }
    private fun facebookAuthentication() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(
            callbackManager!!,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("FacebookAuth", "facebook:onSuccess:$loginResult")
                    showProgressDialog(R.raw.loading_animation_livepage)
                    handleFacebookSignInResult(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("FacebookAuth", "facebook:onCancel")
                    showToast(getString(R.string.authentication_failed),false)
                }

                override fun onError(error: FacebookException) {
                    Log.d("FacebookAuth", "facebook:onError", error)
                    showToast(getString(R.string.authentication_failed),false)
                }
            },
        )
    }


    private fun initObserver() {
        viewModel.socialLogInResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {}

                is NetworkResponse.Success -> {
                    hideProgressDialog()
                    PrefManager.get().removeKey(APP_STATE)
                    PrefManager.get().save(USER_DATA, response.data?.data)
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                    requireActivity().finish()
                }

                is NetworkResponse.Error -> {
                    hideProgressDialog()
                    showToast(response.message.toString(), false)
                }
            }
        }

        viewModel.logInResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnSignIn.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    binding.btnSignIn.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    if (response.data?.email_verified == "1") {
                        PrefManager.get().removeKey(APP_STATE)
                        PrefManager.get().save(USER_DATA, response.data)
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    } else {
                        otpViewModel.getOtp(OtpReq(binding.etEmail.text.toString(), null))
                    }
                }

                is NetworkResponse.Error -> {
                    binding.btnSignIn.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }

        otpViewModel.otpResendResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnSignIn.visibility = View.INVISIBLE
                    showProgressDialog(R.raw.loading_animation)
                }

                is NetworkResponse.Success -> {
                    binding.btnSignIn.visibility = View.VISIBLE
                    hideProgressDialog()
                    PrefManager.get().save(APP_STATE, SignIn)
                    replaceFragment(R.id.authContainer, OtpFragment(), true, Otp)
                }

                is NetworkResponse.Error -> {
                    binding.btnSignIn.visibility = View.VISIBLE
                    hideProgressDialog()
                    showToast(response.message.toString(), false)
                }
            }
        }
    }

    private fun themeColorControl() {
        if (Utils(requireContext()).isDarkTheme()) {
            binding.emailLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
        } else {
            binding.emailLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQ) {
            binding.progressBar.visibility = View.GONE
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val credential = GoogleAuthProvider.getCredential(task.result.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("GoogleAuth", "signInWithGoogle:success")
                        handleGoogleSignInResult(task)
                    } else {
                        Log.d("GoogleAuth", "signInWithGoogle:failure", task.exception)
                        showToast(getString(R.string.authentication_failed),false)
                    }
                }
            } catch (e: Exception) {
                hideProgressDialog()
                Log.d("GoogleAuth", "error: ${e.printStackTrace()}")
            }
        }

        //for facebook
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val id = account.id
                val email = account.email
                val name = account.displayName
                Log.d("GoogleAuth", "account data: $id, $name, $email")
//                Toast.makeText(requireContext(), "$id, $email, $mame", Toast.LENGTH_SHORT).show()
                viewModel.socialLogIn(
                    SocialLogInReq(
                        id.toString(),
                        email.toString(),
                        name.toString(),
                        GOOGLE
                    )
                )
            } else {
                // Handle the case where account is null
                Log.e("GoogleAuth", "Google sign-in failed: Account is null")
                showToast(getString(R.string.authentication_failed),false)
            }
        } catch (e: ApiException) {
            // Sign in failed, handle error
            Log.e("GoogleAuth", "signInResult:failed code=${e.message.toString()}")
            showToast(getString(R.string.authentication_failed),false)
        }
    }

    private fun handleFacebookSignInResult(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = auth.currentUser
                val id = user?.uid
                val name = user?.displayName
                val email = user?.email
                Log.d("FacebookAuth", "signInWithFacebook:success, $id, $name, $email")
                viewModel.socialLogIn(
                    SocialLogInReq(
                        id.toString(),
                        email.toString(),
                        name.toString(),
                        FACEBOOK
                    )
                )
            } else {
                // If sign in fails, display a message to the user.
                Log.w("FacebookAuth", "signInWithFacebook:failure", task.exception)
                showToast(getString(R.string.authentication_failed),false)
            }
        }
    }
}

