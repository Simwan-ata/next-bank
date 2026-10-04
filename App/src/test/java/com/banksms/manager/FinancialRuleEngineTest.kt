package com.banksms.manager

import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialRuleEngineTest {
    private fun tx(
        id: String,
        amount: Long,
        description: String,
        bank: String = "ملت",
        type: String = "expense"
    ) = Transaction(
        id = id,
        timestamp = 1L,
        type = type,
        amount = amount,
        description = description,
        bank = bank
    )

    @Test
    fun higherPriorityRuleWinsDeterministically() {
        val engine = FinancialRuleEngine(
            listOf(
                FinancialRule("low", "All", 10, category = "سایر"),
                FinancialRule("high", "Fuel", 20, merchant = "پمپ", category = "حمل‌ونقل")
            )
        )

        val result = engine.apply(listOf(tx("1", 100_000, "پمپ بنزین")))

        assertEquals("حمل‌ونقل", result.single().category)
    }

    @Test
    fun previewCountsOnlyMatchingRules() {
        val engine = FinancialRuleEngine(
            listOf(FinancialRule("fuel", "Fuel", 10, merchant = "پمپ", category = "حمل‌ونقل"))
        )

        val counts = engine.preview(
            listOf(tx("1", 100_000, "پمپ"), tx("2", 200_000, "فروشگاه"))
        )

        assertEquals(1, counts["fuel"])
    }
}
