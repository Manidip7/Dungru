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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentSignUpBinding
import com.aksar.dungru.models.request.SignUpReq
import com.aksar.dungru.models.request.SocialLogInReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.home.HomeActivity
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.Constance.GOOGLE
import com.aksar.dungru.utils.Constance.Otp
import com.aksar.dungru.utils.Constance.SignIn
import com.aksar.dungru.utils.Constance.SignUp
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.APP_STATE
import com.aksar.dungru.utils.PrefManager.Companion.USER_DATA
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.hideProgressDialog
import com.aksar.dungru.utils.extensions.isAllFieldAreValidated
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.setPasswordVisible
import com.aksar.dungru.utils.extensions.showProgressDialog
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.AuthViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.facebook.AccessToken
import com.facebook.CallbackManager
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

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel
    private val GOOGLE_SIGN_IN_REQ = 111
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var callbackManager: CallbackManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        themeColorControl()

        //ViewModel initialization
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[AuthViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(requireContext())
        callbackManager = CallbackManager.Factory.create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constance.WebClientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        initObserver()
        setPasswordVisible(requireContext(), binding.etPassword)
        setPasswordVisible(requireContext(), binding.etCnfPassword)

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    binding.etEmail.error = null
                else
                    binding.etEmail.error = "invalid email!"
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

        binding.etCnfPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 8)
                    binding.etCnfPassword.error = "Password must be at least 8 characters"
                else
                    binding.etCnfPassword.error = null
            }
        })

        binding.btnHaveAcc.setOnClickListener {
            replaceFragment(R.id.authContainer, SignInFragment(), false, SignIn)
        }

        binding.btnSignUp.setOnClickListener {
            if (isAllFieldAreValidated(
                    binding.etName,
                    binding.etEmail,
                    binding.etPassword,
                    binding.etCnfPassword
                )
            ) {
                PrefManager.get()
                    .save(PrefManager.TEMP_EMAIL, binding.etEmail.text.toString().trim())
                if (binding.etPassword.text.toString() == binding.etCnfPassword.text.toString()) {
                    viewModel.registerUser(
                        SignUpReq(
                            binding.etEmail.text.toString(),
                            binding.etCnfPassword.text.toString(),
                            binding.etName.text.toString()
                        )
                    )
                } else {
                    binding.etPassword.error = "Password not matched"
                    binding.etCnfPassword.error = "Password not matched"
                }
            }
        }

        binding.btnGoogle.setOnClickListener {
            if(NetworkUtils.IS_CONNECTED) googleSignIn()
        }

        binding.btnFacebook.setOnClickListener {
            if (NetworkUtils.IS_CONNECTED) facebookAuthentication()
        }

        return binding.root
    }
    private fun googleSignIn() {
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

    private fun themeColorControl() {
        if (Utils(requireContext()).isDarkTheme()) {
            binding.nameLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
            binding.emailLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
            binding.cnfPasswordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
        } else {
            binding.nameLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
            binding.emailLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
            binding.cnfPasswordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
        }
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

        viewModel.signUpResponse.observe(viewLifecycleOwner) { response ->
            Log.d("RES", "${response.data?.message}")
            when (response) {

                is NetworkResponse.Loading -> {
                    binding.btnSignUp.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    binding.btnSignUp.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    showToast(response.data?.message.toString(), false)
                    val data = LoggedUserData(
                        user_id = response.data?.data?.user_id.toString(),
                        unique_token = response.data?.data?.unique_token.toString(),
                        username = binding.etName.text.toString(),
                        unique_name = null,
                        usertype = null,
                        email = binding.etEmail.text.toString(),
                        phone = null,
                        dob = null,
                        gender = null,
                        added_on = null,
                        coin_purchase = null,
                        coin_sent = null,
                        no_of_coins = null,
                        coins_earned = null,
                        email_verified = null,
                        general_setting = null,
                        image = null,
                        in_app_notifications = null
                    )
                    PrefManager.get().save(USER_DATA, data)
                    PrefManager.get().save(APP_STATE, SignUp)
                    replaceFragment(R.id.authContainer, OtpFragment.newInstance(SignUp), false, Otp)
                }

                is NetworkResponse.Error -> {
                    binding.btnSignUp.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
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
                        Log.d("GoogleAuth", "signInWithCredential:success")
                        handleGoogleSignInResult(task)
                    } else {
                        Log.d("GoogleAuth", "signInWithCredential:failure", task.exception)
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
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
                Log.d("GoogleAuth", "handleSignInResult:$name, $email, $id ")
                viewModel.socialLogIn(SocialLogInReq(id.toString(),email.toString(), name.toString(), GOOGLE))
            } else {
                // Handle the case where account is null
                Log.e("GoogleAuth", "Google sign-in failed: Account is null")
                Toast.makeText(
                    requireContext(),
                    "Google sign-in failed: Account is null",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: ApiException) {
            // Sign in failed, handle error
            Log.e("GoogleAuth", "signInResult:failed code=${e.message.toString()}")
            Toast.makeText(requireContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show()
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
                        Constance.FACEBOOK
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