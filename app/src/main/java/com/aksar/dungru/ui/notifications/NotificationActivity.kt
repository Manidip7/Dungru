package com.aksar.dungru.ui.notifications

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.adapters.NotificationAdapter
import com.aksar.dungru.databinding.ActivityNotificationBinding
import com.aksar.dungru.models.NotificationModel
import com.aksar.dungru.utils.NetworkUtils

class NotificationActivity : AppCompatActivity(), OnClickListener {
    lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        NetworkUtils.checkConnection(this)

        val notificationItemsList = arrayListOf<NotificationModel>(
            NotificationModel("New Message", "12.30 pm", "Hey!You Have a New message from John"),
            NotificationModel("Coin Added", "2.10 am", "Congats!You have earned a new Coin"),
            NotificationModel("Live!!", "12.00 am", "Hi!Your Friend Joy is live now."),
            NotificationModel(
                "1 New Follower",
                "6.30 pm",
                "Hi there!!John has started follwing you."
            ),
        )

        val notificationRecycler = binding.recyclerNotification
        adapter = NotificationAdapter(notificationItemsList) {
        }
        notificationRecycler.adapter = adapter
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnBack -> {
                onBackPressed()
            }
        }
    }
}