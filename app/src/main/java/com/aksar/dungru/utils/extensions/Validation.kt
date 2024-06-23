package com.aksar.dungru.utils.extensions

import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun isAllFieldAreValidated(vararg fields: TextInputEditText): Boolean {
    var isValid = true
    for (field in fields) {
        if (field.text.toString().isEmpty()) {
            field.error = "This Field is required"
            isValid = false
        } else {
            if (field.error != null) {
                isValid = false
            }
        }
    }
    return isValid
}

fun timeFormatter(inputDateTime: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val postDateTime = inputFormat.parse(inputDateTime) ?: return ""

    val currentTimeMillis = System.currentTimeMillis()
    val postTimeMillis = postDateTime.time

    val diffMillis = currentTimeMillis - postTimeMillis

    val minutes = diffMillis / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        else -> "$days days ago"
    }
}


