package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class AddReportReq(
    val user_id: String,
    val post_id: String,
    val report_reason: String
)