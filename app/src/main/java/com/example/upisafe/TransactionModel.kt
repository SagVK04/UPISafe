package com.example.upisafe

data class TransactionModel(
    val amount: String,
    val date: String,
    val time: String,
    val platform: String,
    val result: String
)