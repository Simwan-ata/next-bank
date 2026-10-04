package com.banksms.manager

import java.util.Locale
import java.util.regex.Pattern

object SmsTransactionParser {
    private val amountPatterns = listOf(
        Pattern.compile("(?:مبلغ(?:\\s+تراکنش)?|مبلغ خرید|مبلغ واریز|مبلغ برداشت)\\s*[:：]?\\s*([0-9۰-۹][0-9۰-۹,٬. ]+)"),
        Pattern.compile("(?:خرید|برداشت|واریز|دریافت)\\s*[:：]?\\s*([0-9۰-۹][0-9۰-۹,٬. ]+)"),
        Pattern.compile("([0-9۰-۹][0-9۰-۹,٬. ]{3,})\\s*(?:ریال|ريال|تومان|تومن)")
    )
    private val cardPattern = Pattern.compile("(?:کارت|card)[^۰-۹0-9]{0,20}([۰-۹0-9]{4})", Pattern.CASE_INSENSITIVE)
    private val bankNames = listOf(
        "ملت" to "ملت", "ملی" to "ملی", "سامان" to "سامان", "صادرات" to "صادرات",
        "تجارت" to "تجارت", "رفاه" to "رفاه", "پارسیان" to "پارسیان",
        "پاسارگاد" to "پاسارگاد", "کشاورزی" to "کشاورزی", "مهر" to "مهر",
        "رسالت" to "رسالت", "موسسه ملل" to "ملل"
    )

    fun parse(address: String?, body: String, timestamp: Long): Transaction? {
        val text = normalize(body)
        val amount = extractAmount(text) ?: return null

        val type = when {
            Regex("برداشت|خرید|پرداخت|کسر").containsMatchIn(text) -> "expense"
            Regex("واریز|دریافت|افزایش").containsMatchIn(text) -> "income"
            else -> return null
        }

        if (Regex("موجودی").containsMatchIn(text) &&
            !Regex("خرید|برداشت|واریز|دریافت|پرداخت").containsMatchIn(text)
        ) return null

        val bank = bankNames.firstOrNull { text.contains(it.first) }?.second ?: ""
        val card = cardPattern.matcher(text).let { if (it.find()) it.group(1) ?: "" else "" }
        val merchant = extractMerchant(text)
        val category = when {
            text.contains("خرید") || text.contains("فروشگاه") -> "خرید"
            text.contains("بنزین") || text.contains("سوخت") -> "حمل‌ونقل"
            text.contains("قبض") || text.contains("آب") || text.contains("برق") || text.contains("گاز") -> "قبوض"
            text.contains("حقوق") || text.contains("دستمزد") -> "حقوق"
            else -> if (type == "income") "درآمد" else "سایر"
        }

        val stableSource = "${address ?: ""}|$timestamp|$amount|${text.take(180)}"
        return Transaction(
            id = stableSource.hashCode().toString(),
            timestamp = timestamp,
            type = type,
            amount = amount,
            description = merchant.ifBlank { if (type == "income") "واریز وجه" else "برداشت" },
            bank = bank,
            cardLast4 = card,
            source = "sms",
            confidence = confidence(text, bank, merchant),
            raw = body
        )
    }

    private fun extractAmount(text: String): Long? {
        for (pattern in amountPatterns) {
            val m = pattern.matcher(text)
            if (!m.find()) continue
            val raw = m.group(1)?.replace(",", "")?.replace("٬", "")?.replace(".", "")?.replace(" ", "") ?: continue
            val tail = text.substring(m.start(), (m.end() + 12).coerceAtMost(text.length))
            val value = raw.toLongOrNull() ?: continue
            return if (tail.contains("تومان") || tail.contains("تومن")) value * 10 else value
        }
        return null
    }

    private fun extractMerchant(text: String): String {
        val patterns = listOf(
            Regex("پذیرنده\\s*[:：]?\\s*([^،\\n]+)"),
            Regex("فروشگاه\\s*[:：]?\\s*([^،\\n]+)")
        )
        return patterns.firstNotNullOfOrNull { it.find(text)?.groupValues?.getOrNull(1)?.trim() } ?: ""
    }

    private fun confidence(text: String, bank: String, merchant: String): Int {
        var score = 35
        if (Regex("مبلغ|خرید|برداشت|واریز|دریافت").containsMatchIn(text)) score += 25
        if (bank.isNotBlank()) score += 15
        if (merchant.isNotBlank()) score += 10
        if (Regex("کارت|پیگیری|مرجع|تراکنش").containsMatchIn(text)) score += 10
        return score.coerceAtMost(100)
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
        }.joinToString("").lowercase(Locale("fa"))
}