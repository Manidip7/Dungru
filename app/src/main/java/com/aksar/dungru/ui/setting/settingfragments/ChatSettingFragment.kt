package com.aksar.dungru.ui.setting.settingfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aksar.dungru.databinding.FragmentChatSettingBinding

class ChatSettingFragment : Fragment() {
    lateinit var binding: FragmentChatSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChatSettingBinding.inflate(layoutInflater)


        binding.btnLastTimeSeen.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(requireContext(), "On", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnAutoPlayVideos.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(requireContext(), "On", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnReadReceipts.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(requireContext(), "On", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }


        return binding.root
    }
}