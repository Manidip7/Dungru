package com.aksar.dungru.ui.home.homefragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aksar.dungru.R
import com.aksar.dungru.adapters.ChatListAdapter
import com.aksar.dungru.databinding.FragmentChatListBinding
import com.aksar.dungru.models.ChatListDataModel
import com.aksar.dungru.ui.chat.ChatActivity
import com.aksar.dungru.utils.Utils


class ChatsListFragment : Fragment() {
    private lateinit var binding: FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(layoutInflater,container,false)
        themeColorControl()

        val chatItemList = arrayListOf<ChatListDataModel>(
            ChatListDataModel(true,R.drawable.demo_profile_img,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_profile_img,"Barnali","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_content_image,"Saif","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_profile_img,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_profile_img,"Barnali","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_content_image,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_profile_img,"Saif","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_feed_img,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_content_image,"Saif","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_profile_img,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_profile_img,"Barnali","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_profile_img,"Saif","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_content_image,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_profile_img,"Barnali","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_profile_img,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_content_image,"Saif","10.12 AM", "Hii"),
            ChatListDataModel(true,R.drawable.demo_profile_img,"Mugdho","10.12 AM", "Hii"),
            ChatListDataModel(false,R.drawable.demo_profile_img,"Saif","10.12 AM", "Hii"),
        )
        val chatItemRecycler = binding.chatRecyclerView
        chatListAdapter = ChatListAdapter(chatItemList){
            val intent = Intent(requireContext(),ChatActivity::class.java)
            intent.putExtra("Data",it)
            startActivity(intent)
        }
        chatItemRecycler.adapter = chatListAdapter

        return binding.root
    }


    private fun themeColorControl() {
        if (Utils(requireContext()).isDarkTheme()) {
            binding.chatRecyclerView.setBackgroundColor(resources.getColor(R.color.black))
        } else {
            binding.chatRecyclerView.setBackgroundColor(resources.getColor(R.color.white))
        }
    }
}