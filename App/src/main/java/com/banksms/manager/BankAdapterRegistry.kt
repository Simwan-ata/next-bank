package com.banksms.manager

class BankAdapterRegistry(
    private val adapters: List<BankAdapter> = listOf(GenericBankAdapter())
) {
    fun parse(sender: String?, body: String?, timestamp: Long): List<ParsedBankEvent> {
        val adapter = adapters.firstOrNull { it.canHandle(sender, body) } ?: return emptyList()
        return adapter.parse(sender, body, timestamp)
    }
}
