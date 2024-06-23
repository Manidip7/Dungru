package com.aksar.dungru.models

import java.io.Serializable

data class ChatListDataModel(
    val isOnline : Boolean,
    val profileImg:Int,
    val name : String,
    val time : String,
    val desc: String,
):Serializable
