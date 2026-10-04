package com.banksms.manager

import org.junit.Assert.assertEquals
import org.junit.Test

class BankAdapterRegistryTest {
    @Test
    fun registryUsesFirstMatchingAdapter() {
        val adapter = object : BankAdapter {
            override val bankId = "test"
            override fun canHandle(sender: String?, body: String?) = true
            override fun parse(sender: String?, body: String?, timestamp: Long) =
                listOf(ParsedBankEvent("x", timestamp, EventType.INCOME, amount = 100))
        }

        val events = BankAdapterRegistry(listOf(adapter)).parse("TEST", "واریز 1000 ریال", 10L)

        assertEquals("x", events.single().id)
        assertEquals(EventType.INCOME, events.single().eventType)
    }
}
