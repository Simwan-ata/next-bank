package com.banksms.manager

/**
 * A balance explicitly reported by the bank.
 * This is not a transaction and must never be synthesized into one.
 */
data class BalanceObservation(
    val id: String,
    val timestamp: Long,
    val balance: Long,
    val currency: Currency = Currency.IRR,
    val bank: String = "",
    val cardLast4: String = "",
    val accountFingerprint: String = "",
    val sourceEventId: String = "",
    val confidence: Int = 0,
    val raw: String = ""
)
