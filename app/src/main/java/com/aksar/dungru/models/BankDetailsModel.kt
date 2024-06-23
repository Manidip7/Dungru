package com.aksar.dungru.models

data class BankDetailsModel(
    val bankImage:Int,
    val bankName: String,
    val holderName: String,
    val accountNumber: String,
    val ifscCode: String
)
