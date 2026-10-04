package com.banksms.manager

import java.util.regex.Pattern
import java.util.UUID

object SmsTransactionParser {
    private val amountPattern = Pattern.compile(
        "(?:مبلغ|مبلغ تراکنش|برداشت|واریز|خرید|انتقال)[^۰-۹0-9]{0,30}([۰-۹0-9][۰-۹0-9,٬. ]{2,})",
        Pattern.CASE_INSENSITIVE
    )
    private val cardPattern = Pattern.compile("(?:کارت|card)[^۰-۹0-9]{0,15}([۰-۹0-9]{4})")
    private val bankNames = listOf(
        "ملت" to "ملت", "ملی" to "ملی", "سامان" to "سامان",
        "صادرات" to "صادرات", "تجارت" to "تجارت", "رفاه" to "رفاه",
        "پارسیان" to "پارسیان", "پاسارگاد" to "پاسارگاد",
        "کشاورزی" to "کشاورزی", "مهر" to "مهر", "رسالت" to "رسالت"
    )

    fun parse(address: String?, body: String, timestamp: Long): Transaction? {
        val normalized = normalizeDigits(body)
        val amount = extractAmount(normalized) ?: return null
        val type = when {
            normalized.contains("برداشت") || normalized.contains("خرید") -> "expense"
            normalized.contains("واریز") || normalized.contains("دریافت") -> "income"
            normalized.contains("انتقال") -> if (normalized.contains("واریز")) "income" else "transfer"
            else -> return null
        }
        val bank = bankNames.firstOrNull { normalized.contains(it.first) }?.second ?: ""
        val card = cardPattern.matcher(normalized).let { if (it.find()) it.group(1) ?: "" else "" }
        val description = when {
            normalized.contains("خرید") -> "خرید"
            normalized.contains("انتقال") -> "انتقال وجه"
            normalized.contains("برداشت") -> "برداشت"
            else -> "واریز وجه"
        }
        val stable = (address ?: "") + "|" + timestamp + "|" + amount + "|" + normalized.take(120)
        return Transaction(
            id = stable.hashCode().toString(),
            timestamp = timestamp,
            type = type,
            amount = amount,
            description = description,
            bank = bank,
            cardLast4 = card,
            source = "sms",
            confidence = calculateConfidence(normalized, amount, bank),
            raw = body
        )
    }

    private fun extractAmount(text: String): Long? {
        val matcher = amountPattern.matcher(text)
        if (!matcher.find()) return null
        val digits = matcher.group(1)
            ?.replace(",", "")
            ?.replace("٬", "")
            ?.replace(".", "")
            ?.replace(" ", "")
            ?: return null
        return digits.toLongOrNull()?.takeIf { it > 0 }
    }

    private fun calculateConfidence(text: String, amount: Long, bank: String): Int {
        var score = 50
        if (amount > 0) score += 20
        if (bank.isNotBlank()) score += 15
        if (text.contains("تراکنش") || text.contains("موجودی")) score += 10
        return score.coerceAtMost(100)
    }

    private fun normalizeDigits(input: String): String =
        input.map { c ->
            when (c) {
                in '۰'..'۹' -> ('0'.code + (c.code - '۰'.code)).toChar()
                in '٠'..'٩' -> ('0'.code + (c.code - '٠'.code)).toChar()
                else -> c
            }
        }.joinToString("")
}