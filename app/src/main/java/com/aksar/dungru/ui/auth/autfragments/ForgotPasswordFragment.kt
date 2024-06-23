package com.aksar.dungru.ui.auth.autfragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentForgotPasswordBinding
import com.aksar.dungru.models.request.OtpReq
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.Constance.ForgotPassword
import com.aksar.dungru.utils.Constance.Otp
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.isAllFieldAreValidated
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.OtpViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

class ForgotPasswordFragment : Fragment(){
    private lateinit var binding : FragmentForgotPasswordBinding
    private lateinit var viewModel : OtpViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[OtpViewModel::class.java]
        initObserver()
        themeColorControl()

        binding.etEmail.addTextChangedListener( object : TextWatcher {
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

        binding.btnBack.setOnClickListener{
            requireActivity().onBackPressed()
        }
        binding.btnSend.setOnClickListener {
            if(isAllFieldAreValidated(binding.etEmail)){
                PrefManager.get().save(PrefManager.TEMP_EMAIL,binding.etEmail.text.toString().trim())
                viewModel.getOtp(OtpReq(binding.etEmail.text.toString().trim(), null))
            }
        }
        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance(): ForgotPasswordFragment {
            return ForgotPasswordFragment()
        }
    }
    private fun themeColorControl() {
        if (Utils(requireContext()).isDarkTheme()) {
            binding.emailLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_black)
        } else {
            binding.emailLayout.setBackgroundResource(R.drawable.bg_rounded_text_gradian_color_stroke_white)
        }
    }

    private fun initObserver() {
        viewModel.otpResendResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnSend.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(response.data?.response.toString(), false)
                    replaceFragment(R.id.authContainer, OtpFragment.newInstance(ForgotPassword),true,Otp)
                }

                is NetworkResponse.Error -> {
                    binding.btnSend.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }
    }



}