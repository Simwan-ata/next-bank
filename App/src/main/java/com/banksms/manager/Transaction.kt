package com.banksms.manager

data class Transaction(
    val id: String,
    val timestamp: Long,
    val type: String,
    val amount: Long,
    val description: String,
    val bank: String,
    val cardLast4: String = "",
    val person: String = "",
    val category: String = "",
    val source: String = "sms",
    val confidence: Int = 0,
    val needsReview: Boolean = false,
    val raw: String = ""
)