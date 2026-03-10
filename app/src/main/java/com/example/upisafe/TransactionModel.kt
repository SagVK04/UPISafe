package com.example.upisafe

data class TransactionModel(
    val amount: String = "",
    val date: String = "",
    val time: String = "",
    val id: String = "",
    val tr_type: String = "",
    val result: String = "", // "Safe" or "Fraud"
    val riskScore: String = "", // "85%"
    val timestamp: Long = System.currentTimeMillis()
)