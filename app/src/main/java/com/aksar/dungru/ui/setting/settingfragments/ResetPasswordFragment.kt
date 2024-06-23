package com.aksar.dungru.ui.setting.settingfragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.databinding.FragmentResetPasswordBinding
import com.aksar.dungru.models.request.ResetPassReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.isAllFieldAreValidated
import com.aksar.dungru.utils.extensions.setPasswordVisible
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.PasswordViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

@Suppress("DEPRECATION")
class ResetPasswordFragment : Fragment() {
    lateinit var binding: FragmentResetPasswordBinding
    private lateinit var viewModel: PasswordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[PasswordViewModel::class.java]

        initObserver()
        setPasswordVisible(requireContext(), binding.etNewPasswordLayout.editText)
        setPasswordVisible(requireContext(), binding.etCnfPasswordLayout.editText)

        binding.etNewPasswordLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 8)
                    binding.etNewPasswordLayout.error = "Password must be at least 8 characters"
                else
                    binding.etNewPasswordLayout.error = null
            }
        })

        binding.etCnfPasswordLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 8)
                    binding.etCnfPasswordLayout.error = "Password must be at least 8 characters"
                else
                    binding.etCnfPasswordLayout.error = null
            }
        })

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnChangePassword.setOnClickListener {
            if (binding.etOldPasswordLayout.editText?.editableText.toString().isNotEmpty())
                requireActivity().onBackPressed()
            else
                binding.etOldPasswordLayout.error = "Enter Old Password"

        }

        binding.btnChangePassword.setOnClickListener {
            if (
                isAllFieldAreValidated(
                    binding.etOldPassword,
                    binding.etnewpassword,
                    binding.etconfirmpassword
                )
            ) {
                if (binding.etnewpassword.text.toString() == binding.etconfirmpassword.text.toString()) {
                    val data = PrefManager.get()
                        .getObject(PrefManager.USER_DATA, LoggedUserData::class.java)

                    viewModel.resetPassword(
                        ResetPassReq(
                            binding.etnewpassword.text.toString(),
                            binding.etOldPassword.text.toString(),
                            data?.unique_token.toString(),
                            data!!.user_id.toInt()
                        )
                    )

                } else {
                    binding.etnewpassword.error = "Password not matched!"
                    binding.etconfirmpassword.error = "Password not matched!"
                }
            }
        }

        return binding.root
    }

    private fun initObserver() {
        viewModel.resetPwdResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnChangePassword.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    showToast(response.data?.response.toString(), false)
                    requireActivity().onBackPressed()
                }

                is NetworkResponse.Error -> {
                    binding.btnChangePassword.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}