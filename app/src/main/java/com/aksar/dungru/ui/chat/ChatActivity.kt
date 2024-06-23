package com.aksar.dungru.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityChatBinding
import com.aksar.dungru.models.ChatListDataModel
import com.aksar.dungru.utils.NetworkUtils
import com.google.android.material.textfield.TextInputEditText
import com.vdurmont.emoji.Emoji


class ChatActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatData: ChatListDataModel
    private lateinit var editText: TextInputEditText
    private lateinit var emojiAdapter: EmojiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatData = intent.getSerializableExtra("Data") as ChatListDataModel


        binding.profileImg.setImageResource(chatData.profileImg)
        binding.txtName.text = chatData.name
        if (chatData.isOnline)
            binding.txtStatus.text = this.getString(R.string.online)
        else
            binding.txtStatus.text = this.getString(R.string.offline)

        NetworkUtils.checkConnection(this)


        editText = binding.etChat
        val recyclerView:RecyclerView = binding.emojiRecycler
        recyclerView.layoutManager = GridLayoutManager(this,7)

        binding.emojiRecycler.visibility = View.GONE

        binding.btnEmoji.setOnClickListener {
            if (recyclerView.visibility == View.VISIBLE) {
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
                emojiAdapter = EmojiAdapter(getEmojis())
                recyclerView.adapter = emojiAdapter
            }
        }
        binding.btnAttachment.setOnClickListener {
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            if (requestCode == 101){
                val uri = data?.data
                Log.d("onActivityResult: ",uri.toString())
            }
        }
    }
    private fun getEmojis(): List<Emoji> {
        val emojis:MutableList<Emoji> = mutableListOf()
        for (emoji in com.vdurmont.emoji.EmojiManager.getAll()){
            emojis.add(emoji)
        }
        return emojis
    }
    fun onEmojiClicked(view: View){
        val button = view as Button
        val emoji = button.tag as Emoji?
        emoji?.let {
            val emojiUnicode = it.unicode
            val start = maxOf(editText.selectionStart,0)
            val end = maxOf(editText.selectionEnd,0)
            editText.text?.replace(
                minOf(start,end),
                maxOf(start,end),
                emojiUnicode,
                0,
                emojiUnicode.length
            )
        }
    }

    private inner class EmojiAdapter(private val emojis:List<Emoji>):RecyclerView.Adapter<EmojiViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_emoji,parent,false)
            return EmojiViewHolder(view)
        }

        override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
            val emoji = emojis[position]
            holder.button.text = emoji.unicode
            holder.button.tag = emoji
        }

        override fun getItemCount(): Int {
            return emojis.size
        }

    }
    private inner class EmojiViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val button:Button = itemView.findViewById(R.id.btn_emoji)
    }

    override fun onClick(view: View?) {
       when(view){
           binding.btnBack->{
               onBackPressed()
           }
       }
    }
}




