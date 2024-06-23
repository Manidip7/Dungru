package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class CodeAndMsgBaseRes(
    val message: String,
    val respCode: Int
)