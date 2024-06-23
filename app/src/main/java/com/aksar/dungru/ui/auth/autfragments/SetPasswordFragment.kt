package com.aksar.dungru.ui.auth.autfragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentSetPasswordBinding
import com.aksar.dungru.models.request.SetPasswordReq
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.Constance.SignIn
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.TEMP_EMAIL
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.clearFragmentBackstack
import com.aksar.dungru.utils.extensions.isAllFieldAreValidated
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.setPasswordVisible
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.PasswordViewModel
import com.aksar.dungru.viewModel.ViewModelFactory


class SetPasswordFragment : Fragment() {
    private lateinit var viewModel: PasswordViewModel
    private lateinit var binding: FragmentSetPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetPasswordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[PasswordViewModel::class.java]

        initObserver()

        themeColorControl()

        setPasswordVisible(requireContext(), binding.etPassword)
        setPasswordVisible(requireContext(), binding.etCnfPassword)



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

        binding.btnBack.setOnClickListener {
                requireActivity().onBackPressed()
        }
        binding.btnSubmit.setOnClickListener {
            if (isAllFieldAreValidated(binding.etPassword, binding.etCnfPassword)) {
                if (binding.etPassword.text.toString() == binding.etCnfPassword.text.toString()) {
                    viewModel.setNewPassword(
                        SetPasswordReq(
                            PrefManager.get().getString(TEMP_EMAIL,"").toString(),
                            binding.etCnfPassword.text.toString()
                        )
                    )
                } else {
                    binding.etPassword.error = "Password not matched!"
                    binding.etCnfPassword.error = "Password not matched!"
                }
            }
        }
        return binding.root
    }

    private fun themeColorControl() {
        if (Utils(requireContext()).isDarkTheme()) {
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
            binding.cnfPasswordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
        } else {
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
            binding.cnfPasswordLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
        }
    }

    private fun initObserver() {
        viewModel.forgotPwdResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnSubmit.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    showToast(response.data?.message.toString(), false)
                    clearFragmentBackstack()
                    PrefManager.get().removeKey(TEMP_EMAIL)
                    replaceFragment(R.id.authContainer, SignInFragment(), false, SignIn)
                }

                is NetworkResponse.Error -> {
                    binding.btnSubmit.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(),false)
                }
            }
        }
    }


}