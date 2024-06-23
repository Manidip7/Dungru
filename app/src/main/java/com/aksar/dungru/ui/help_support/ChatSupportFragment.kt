package com.aksar.dungru.ui.help_support

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksar.dungru.databinding.FragmentChatSupportBinding
import com.aksar.dungru.utils.Utils

class ChatSupportFragment : Fragment() {
    private lateinit var binding: FragmentChatSupportBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentChatSupportBinding.inflate(layoutInflater)
        themeColorControl()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun themeColorControl() {
        if (Utils(requireContext()).isDarkTheme()) {
            binding.bottomLayout.setBackgroundColor(resources.getColor(com.google.android.material.R.color.material_dynamic_neutral_variant20))
        } else {
            binding.bottomLayout.setBackgroundColor(resources.getColor(com.google.android.material.R.color.material_dynamic_neutral_variant95))
        }
    }
}