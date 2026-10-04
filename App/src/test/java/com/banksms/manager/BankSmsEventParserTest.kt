package com.banksms.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BankSmsEventParserTest {
    @Test
    fun balanceOnlySmsProducesBalanceObservationEventNotTransaction() {
        val events = BankSmsEventParser.parse("ملت", "موجودی: 1,250,000 ریال", 100L)
        assertEquals(1, events.size)
        assertEquals(EventType.BALANCE_ONLY, events.single().eventType)
        assertEquals(1_250_000L, events.single().balanceAfter)
        assertTrue(events.single().needsReview.not())
    }

    @Test
    fun transferSmsIsClassifiedAsTransfer() {
        val events = BankSmsEventParser.parse("ملت", "انتقال وجه مبلغ 500000 ریال", 100L)
        assertTrue(events.any { it.eventType == EventType.TRANSFER })
    }

    @Test
    fun unknownSmsIsReviewable() {
        val events = BankSmsEventParser.parse("X", "پیام بانکی نامشخص", 100L)
        assertTrue(events.any { it.eventType == EventType.UNKNOWN && it.needsReview })
    }
}
