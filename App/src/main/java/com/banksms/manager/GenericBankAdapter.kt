package com.banksms.manager

/**
 * Fallback adapter for SMS formats not yet covered by a bank-specific adapter.
 * Low-confidence results are surfaced for review rather than silently treated
 * as authoritative financial facts.
 */
class GenericBankAdapter : BankAdapter {
    override val bankId: String = "generic"

    override fun canHandle(sender: String?, body: String?): Boolean =
        !body.isNullOrBlank()

    override fun parse(sender: String?, body: String?, timestamp: Long): List<ParsedBankEvent> {
        val transaction = SmsTransactionParser.parse(sender, body, timestamp) ?: return emptyList()
        val eventType = when (transaction.type) {
            "income" -> EventType.INCOME
            "expense" -> EventType.EXPENSE
            else -> EventType.UNKNOWN
        }
        return listOf(
            ParsedBankEvent(
                id = transaction.id,
                timestamp = transaction.timestamp,
                eventType = eventType,
                amount = transaction.amount,
                bank = transaction.bank,
                cardLast4 = transaction.cardLast4,
                merchantRaw = transaction.description,
                source = transaction.source,
                confidence = transaction.confidence,
                needsReview = transaction.needsReview,
                raw = transaction.raw
            )
        )
    }
}
