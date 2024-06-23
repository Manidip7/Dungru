package com.aksar.dungru.ui.setting.settingfragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aksar.dungru.R
import com.aksar.dungru.databinding.CustomDiglogFeedbackBinding
import com.aksar.dungru.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {
    private lateinit var binding: FragmentFeedbackBinding
    private var selectedRatting: String = ""
    private var selectedImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentFeedbackBinding.inflate(layoutInflater)
        binding.btnVeryBad.setOnClickListener {
            feedBackButtonControl(binding.imgVeryBad, "Very Bad")
        }
        binding.btnBad.setOnClickListener {
            feedBackButtonControl(binding.imgBad, "Bad")
        }
        binding.btnNeutral.setOnClickListener {
            feedBackButtonControl(binding.imgNeutral, "Neutral")
        }
        binding.btnGood.setOnClickListener {
            feedBackButtonControl(binding.imgGood, "Good")
        }
        binding.btnVeryGood.setOnClickListener {
            feedBackButtonControl(binding.imgVeryGood, "Very Good")
        }
        binding.btnSendFeedback.setOnClickListener {
            showSuccessDialog()
        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun showSuccessDialog() {
        val dialogBinding = CustomDiglogFeedbackBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
            dialog.show()
        }

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            requireActivity().onBackPressed()
        }
    }

    private fun feedBackButtonControl(image: ImageView, text: String) {
        this.selectedImage?.let {
            it.background = null
        }
        selectedRatting = text
        image.background = resources.getDrawable(R.drawable.bg_feedback_image_background)
        this.selectedImage = image
        Toast.makeText(requireContext(), selectedRatting, Toast.LENGTH_SHORT).show()
    }

}
