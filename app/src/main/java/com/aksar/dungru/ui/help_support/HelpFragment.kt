package com.aksar.dungru.ui.help_support

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksar.dungru.R
import com.aksar.dungru.adapters.FAQAdapter
import com.aksar.dungru.databinding.ContactUsFullsdreenDiglogBinding
import com.aksar.dungru.databinding.FragmentHelpBinding
import com.aksar.dungru.models.FAQItem
import com.aksar.dungru.utils.extensions.replaceFragment


class HelpFragment : Fragment() {
    lateinit var binding : FragmentHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHelpBinding.inflate(layoutInflater)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recHelp.layoutManager = layoutManager

        val data = listOf(
            FAQItem("What is a live streaming app, and how does it work?","Hello"),
            FAQItem("How do I ensure the privacy and security of my live stream?","Hello"),
            FAQItem("Can I monetize my live streams on the app?","Yes, we offer monetization option for content creators, such as virtual gifting from viewers, You can earn money while sharing your content with your audience"),
            FAQItem("Is there a limit to the number of viewers who can watch my live stream?","He hmghmllo"),)

        val adapter = FAQAdapter(data)
        binding.recHelp.adapter = adapter

        binding.btnSendMassage.setOnClickListener {
            replaceFragment(R.id.setting_container,ChatSupportFragment(),true,"ChatSupport")
        }

        binding.btnCallUs.setOnClickListener {
            showSuccessDialog()
        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun showSuccessDialog() {
        val dialogBinding = ContactUsFullsdreenDiglogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), R.style.FullScreenDialogTheme)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.show()
        dialogBinding.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnFirstNumber.setOnClickListener {
            val phoneNumber =dialogBinding.btnNumber1.text.toString()
            if (phoneNumber.isNotEmpty()) {
                initiateCall(phoneNumber)
            } else {
                Toast.makeText(requireContext(), "Phone number is empty", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBinding.btnSecondNumber.setOnClickListener {
            val phoneNumber = dialogBinding.btnNumber2.text.toString()
            if (phoneNumber.isNotEmpty()){
                initiateCall(phoneNumber)
            }else{
                Toast.makeText(requireContext(), "phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initiateCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }


}