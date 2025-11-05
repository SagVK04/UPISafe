package com.example.upisafe

data class TransactionModel(
    val amount: String = "",
    val date: String = "",
    val platform: String = "",
    val result: String = "", // "Safe" or "Fraud"
    val riskScore: String = "", // "85%"
    val time: String = "",
    val timestamp: Long = System.currentTimeMillis()
)