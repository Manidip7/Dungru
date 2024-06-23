package com.aksar.dungru.ui.setting.settingfragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentEmailUpdateOtpBinding
import com.aksar.dungru.models.request.BaseUserIDAndTokenReq
import com.aksar.dungru.models.request.OtpReq
import com.aksar.dungru.models.request.UpdateEmailOtpReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.auth.OtpWatcher
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.USER_DATA
import com.aksar.dungru.utils.extensions.clearFragmentBackstack
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.OtpViewModel
import com.aksar.dungru.viewModel.ProfileViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

class EmailUpdateOtpFragment : Fragment() {

    private lateinit var binding:FragmentEmailUpdateOtpBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var otpViewModel: OtpViewModel
    private var email: String = ""

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private val timerDuration = 60000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEmailUpdateOtpBinding.inflate(layoutInflater)
        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[ProfileViewModel::class.java]

        otpViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[OtpViewModel::class.java]

        email = PrefManager.get().getString(PrefManager.TEMP_EMAIL, "").toString()

        val formattedText = getString(R.string.we_have_send_you_code_in_your_email_address, "<font color=\"#A3A3A3\">($email)</font>")
        val spannedText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.txtUserNote.text = spannedText

        startTimer()
        initObserver()

        binding.otpItem1.addTextChangedListener(
            OtpWatcher(
                binding.otpItem1,
                binding.otpItem2,
                null
            )
        )
        binding.otpItem2.addTextChangedListener(
            OtpWatcher(
                binding.otpItem2,
                binding.otpItem3,
                binding.otpItem1
            )
        )
        binding.otpItem3.addTextChangedListener(
            OtpWatcher(
                binding.otpItem3,
                binding.otpItem4,
                binding.otpItem2
            )
        )
        binding.otpItem4.addTextChangedListener(
            OtpWatcher(
                binding.otpItem4,
                null,
                binding.otpItem3
            )
        )

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnResend.setOnClickListener {
            otpViewModel.getOtp(OtpReq(email, null))
        }

        binding.btnVerify.setOnClickListener {
            if (binding.otpItem1.text.toString().isNotEmpty()
                && binding.otpItem2.text.toString().isNotEmpty()
                && binding.otpItem3.text.toString().isNotEmpty()
                && binding.otpItem4.text.toString().isNotEmpty()
            ) {
                val otp = binding.otpItem1.text.toString() + binding.otpItem2.text + binding.otpItem3.text + binding.otpItem4.text
                val data = PrefManager.get().getObject(USER_DATA,LoggedUserData::class.java)
                otpViewModel.updateEmailOtpVerification(UpdateEmailOtpReq(email,otp,data?.user_id))

            } else {
                Toast.makeText(requireContext(), "Enter valid OTP", Toast.LENGTH_SHORT).show()
            }

        }

        return binding.root
    }
    private fun initObserver() {
        otpViewModel.otpResendResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnResend.text = getString(R.string.resending)
                }

                is NetworkResponse.Success -> {
                    countDownTimer?.cancel()
                    startTimer()
                    showToast(response.data?.response.toString(), false)
                }

                is NetworkResponse.Error -> {
                    showToast(response.message.toString(), false)
                }
            }
        }

        otpViewModel.emailUpdateOtpValidation.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnVerify.visibility = View.GONE
                }

                is NetworkResponse.Success -> {
                    showToast(response.data?.message.toString(), false)
                    val data = PrefManager.get().getObject(USER_DATA,LoggedUserData::class.java)
                    profileViewModel.fetchUserData(BaseUserIDAndTokenReq(data?.user_id.toString(),data?.unique_token.toString()))
                }

                is NetworkResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.visibility = View.VISIBLE
                    showToast(response.message.toString(), false)
                }
            }
        }

        profileViewModel.profileResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnVerify.visibility = View.GONE
                }

                is NetworkResponse.Success -> {
                    showToast("Your email successfully updated to ${response.data?.data?.email} this email id.", false)
                    PrefManager.get().save(USER_DATA, response.data?.data)
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.visibility = View.VISIBLE
                    clearFragmentBackstack()
                    replaceFragment(R.id.setting_container, PrivacySecurityFragment(), true, "PrivacySecurity")
                }

                is NetworkResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.visibility = View.VISIBLE
                    showToast(response.message.toString(), false)
                }
            }
        }
    }

    private fun startTimer() {
        binding.btnResend.text = getString(R.string.resend_code)
        countDownTimer = object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                val timeString = String.format("%02d:%02d", minutes, seconds)
                binding.resendTimer.text = timeString
                binding.btnResend.isClickable = false
                binding.btnResend.setTextColor(requireActivity().getColor(R.color.view_grey_color))
            }

            override fun onFinish() {
                timerRunning = false
                binding.btnResend.isClickable = true
                binding.btnResend.setTextColor(requireActivity().getColor(R.color.text_color_blue))
            }

        }
        countDownTimer?.start()
        timerRunning = true
    }
    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        countDownTimer = null
    }

}