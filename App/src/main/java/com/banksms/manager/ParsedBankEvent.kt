package com.banksms.manager

/**
 * Canonical representation of one financial fact extracted from a bank SMS.
 * It is deliberately broader than Transaction so balance, transfer, bill,
 * installment, recharge and unknown/review events can be represented without
 * forcing every SMS into a ledger transaction.
 */
data class ParsedBankEvent(
    val id: String,
    val timestamp: Long,
    val eventType: EventType,
    val amount: Long? = null,
    val balanceAfter: Long? = null,
    val currency: Currency = Currency.IRR,
    val bank: String = "",
    val cardLast4: String = "",
    val accountFingerprint: String = "",
    val merchantRaw: String = "",
    val counterpartyRaw: String = "",
    val reference: String = "",
    val source: String = "sms",
    val confidence: Int = 0,
    val needsReview: Boolean = false,
    val raw: String = ""
)

enum class EventType {
    INCOME,
    EXPENSE,
    TRANSFER,
    BILL_PAYMENT,
    RECHARGE,
    INSTALLMENT,
    FEE,
    BALANCE_ONLY,
    UNKNOWN
}

enum class Currency {
    IRR,
    TOMAN,
    UNKNOWN
}
