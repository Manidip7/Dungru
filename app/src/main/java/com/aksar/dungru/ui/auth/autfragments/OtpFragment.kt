package com.aksar.dungru.ui.auth.autfragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentOtpBinding
import com.aksar.dungru.models.request.OtpReq
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.auth.OtpWatcher
import com.aksar.dungru.ui.home.HomeActivity
import com.aksar.dungru.utils.Constance.ForgotPassword
import com.aksar.dungru.utils.Constance.SetPassword
import com.aksar.dungru.utils.Constance.SignIn
import com.aksar.dungru.utils.Constance.SignUp
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.APP_STATE
import com.aksar.dungru.utils.PrefManager.Companion.TEMP_EMAIL
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.OtpViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

private const val FLOW = "flow"

class OtpFragment : Fragment() {
    private var flow: String? = null
    private lateinit var binding: FragmentOtpBinding
    private lateinit var viewModel: OtpViewModel
    private var email: String = ""

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private val timerDuration = 60000L
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            flow = it.getString(FLOW)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[OtpViewModel::class.java]

        email = PrefManager.get().getString(TEMP_EMAIL, "").toString()

        val formattedText = getString(R.string.we_have_send_you_code_in_your_email_address, "<font color=\"#A3A3A3\">($email)</font>")
        val spannedText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.txtUserNote.text = spannedText

        startTimer()
        initObserver()

        binding.btnBack.setOnClickListener {
            countDownTimer?.cancel()
            when (flow) {
                SignUp -> replaceFragment(R.id.authContainer, SignUpFragment(), false, SignUp)
                SignIn -> replaceFragment(R.id.authContainer, SignInFragment(), false, SignIn)
                else ->  requireActivity().onBackPressed()
            }

        }

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

        binding.btnVerify.setOnClickListener {
            if (binding.otpItem1.text.toString().isNotEmpty()
                && binding.otpItem2.text.toString().isNotEmpty()
                && binding.otpItem3.text.toString().isNotEmpty()
                && binding.otpItem4.text.toString().isNotEmpty()
            ) {
                val otp =
                    binding.otpItem1.text.toString() + binding.otpItem2.text + binding.otpItem3.text + binding.otpItem4.text

                viewModel.otpVerification(OtpReq(email, otp))

            } else {
                Toast.makeText(requireContext(), "Enter valid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnResend.setOnClickListener {
            viewModel.getOtp(OtpReq(email, null))
        }

        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(flow: String) =
            OtpFragment().apply {
                arguments = Bundle().apply {
                    putString(FLOW, flow)
                }
            }
    }

    private fun initObserver() {
        viewModel.otpResendResponse.observe(viewLifecycleOwner) { response ->
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
        viewModel.otpValidationResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnVerify.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResponse.Success -> {
                    PrefManager.get().removeKey(APP_STATE)
                    countDownTimer?.cancel()
                    binding.progressBar.visibility = View.GONE
                    showToast(response.data?.response.toString(), false)
                    when(flow) {
                        ForgotPassword -> {
                            replaceFragment(
                                R.id.authContainer,
                                SetPasswordFragment(),
                                true,
                                SetPassword
                            )
                        }
                        else -> {
                            PrefManager.get().removeKey(TEMP_EMAIL)
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                }

                is NetworkResponse.Error -> {
                    binding.btnVerify.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
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