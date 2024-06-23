package com.aksar.dungru.ui.setting.settingfragments

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
import com.aksar.dungru.databinding.FragmentUpdateEmailBinding
import com.aksar.dungru.models.request.UpdateEmailReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.Constance.Otp
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.TEMP_EMAIL
import com.aksar.dungru.utils.extensions.isAllFieldAreValidated
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.ProfileViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

private const val FLOW = "flow"

class UpdateEmailFragment : Fragment() {
    private var flow: String? = null
    private lateinit var binding: FragmentUpdateEmailBinding
    private lateinit var viewModel: ProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            flow = it.getString(FLOW)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateEmailBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[ProfileViewModel::class.java]
        initObserver()

        binding.etNewEmailLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.etNewEmailLayout.error = null
                    val data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
                    if (email == data?.email.toString()) {
                        binding.etNewEmailLayout.error = "Please don't enter same email."
                    }else{
                        binding.etNewEmailLayout.error = null
                    }
                }
                else binding.etNewEmailLayout.error = "Invalid email!"

            }
        })

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnProceed.setOnClickListener {
            val data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
            if (binding.etNewEmailLayout.error==null && binding.etNewEmail.text.toString().trim().isNotBlank()) {
                PrefManager.get().save(TEMP_EMAIL, binding.etNewEmail.text.toString().trim())
                viewModel.updateEmail(
                    UpdateEmailReq(
                        binding.etNewEmail.text.toString(),
                        data?.unique_token.toString(),
                        data?.user_id.toString()
                    )
                )
            }else{
                binding.etNewEmailLayout.error = "Please enter your new email."
            }

        }
        return binding.root
    }

    private fun initObserver() {
        viewModel.updateEmailResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnProceed.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnProceed.visibility = View.VISIBLE
                    showToast(response.data?.message.toString(), false)
                    replaceFragment(R.id.setting_container, EmailUpdateOtpFragment(), true, Otp)
                }

                is NetworkResponse.Error -> {
                    showToast(response.message.toString(), false)
                    binding.progressBar.visibility = View.GONE
                    binding.btnProceed.visibility = View.VISIBLE
                }
            }
        }
    }
}