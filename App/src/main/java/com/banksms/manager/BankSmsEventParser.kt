package com.banksms.manager

import java.math.BigInteger

object BankSmsEventParser {
    fun parse(sender: String?, body: String?, timestamp: Long): List<ParsedBankEvent> {
        if (body.isNullOrBlank() || timestamp <= 0L) return emptyList()
        val transaction = SmsTransactionParser.parse(sender, body, timestamp)
        val normalized = normalize(body)
        val events = mutableListOf<ParsedBankEvent>()

        extractBalance(normalized)?.let { balance ->
            events += ParsedBankEvent(
                id = stableId(sender, timestamp, normalized, "balance"),
                timestamp = timestamp,
                eventType = EventType.BALANCE_ONLY,
                balanceAfter = balance,
                currency = if (Regex("تومان|تومن").containsMatchIn(normalized)) Currency.TOMAN else Currency.IRR,
                bank = transaction?.bank.orEmpty(),
                cardLast4 = transaction?.cardLast4.orEmpty(),
                source = "sms",
                confidence = 85,
                needsReview = false,
                raw = body
            )
        }

        if (transaction != null) {
            val type = when {
                Regex("قبض|صورتحساب").containsMatchIn(normalized) -> EventType.BILL_PAYMENT
                Regex("شارژ").containsMatchIn(normalized) -> EventType.RECHARGE
                Regex("قسط|اقساط").containsMatchIn(normalized) -> EventType.INSTALLMENT
                Regex("کارمزد").containsMatchIn(normalized) -> EventType.FEE
                Regex("انتقال|کارت.?به.?کارت|واریز به").containsMatchIn(normalized) -> EventType.TRANSFER
                transaction.type == "income" -> EventType.INCOME
                transaction.type == "expense" -> EventType.EXPENSE
                else -> EventType.UNKNOWN
            }
            events += ParsedBankEvent(
                id = transaction.id,
                timestamp = transaction.timestamp,
                eventType = type,
                amount = transaction.amount,
                currency = if (Regex("تومان|تومن").containsMatchIn(normalized)) Currency.TOMAN else Currency.IRR,
                bank = transaction.bank,
                cardLast4 = transaction.cardLast4,
                merchantRaw = transaction.description,
                source = transaction.source,
                confidence = transaction.confidence,
                needsReview = transaction.needsReview,
                raw = transaction.raw
            )
        }

        if (events.isEmpty()) {
            events += ParsedBankEvent(
                id = stableId(sender, timestamp, normalized, "unknown"),
                timestamp = timestamp,
                eventType = EventType.UNKNOWN,
                bank = transaction?.bank.orEmpty(),
                source = "sms",
                confidence = 15,
                needsReview = true,
                raw = body
            )
        }
        return events.distinctBy { it.id + ":" + it.eventType }
    }

    private fun extractBalance(text: String): Long? {
        val regex = Regex("(?:موجودی|مانده(?: حساب)?)\\s*[:：]?\\s*([0-9][0-9,٬. ]*)\\s*(ریال|ريال|تومان|تومن)?")
        val match = regex.find(text) ?: return null
        val raw = match.groupValues[1].filter { it.isDigit() || it == ',' || it == '٬' || it == '.' }
            .replace(",", "").replace("٬", "").replace(".", "")
        val value = runCatching { BigInteger(raw) }.getOrNull() ?: return null
        val normalized = if (match.groupValues[2].contains("تومان") || match.groupValues[2].contains("تومن"))
            value * BigInteger.TEN else value
        return normalized.takeIf { it > BigInteger.ZERO && it <= BigInteger.valueOf(Long.MAX_VALUE) }?.toLong()
    }

    private fun normalize(input: String): String =
        input.map { c ->
            when (c) {
                in '۰'..'۹' -> ('0'.code + c.code - '۰'.code).toChar()
                in '٠'..'٩' -> ('0'.code + c.code - '٠'.code).toChar()
                'ي' -> 'ی'
                'ك' -> 'ک'
                else -> c
            }
        }.joinToString("")

    private fun stableId(sender: String?, timestamp: Long, text: String, kind: String): String {
        val source = kind + "|" + sender.orEmpty() + "|" + timestamp + "|" + text.take(500)
        return java.security.MessageDigest.getInstance("SHA-256")
            .digest(source.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
