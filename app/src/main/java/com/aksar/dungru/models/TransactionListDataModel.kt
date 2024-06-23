package com.aksar.dungru.models

import java.io.Serializable
data class TransactionListDataModel(
    val transactionType: Int,
    val title : String,
    val dateTime : String,
    val amount: String,
):Serializable
