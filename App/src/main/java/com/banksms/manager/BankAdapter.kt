package com.banksms.manager

/**
 * Bank-specific parsing contract.
 *
 * Adapters are deterministic and local-only. They must not perform network
 * requests or depend on remote services.
 */
interface BankAdapter {
    val bankId: String

    fun canHandle(sender: String?, body: String?): Boolean

    fun parse(sender: String?, body: String?, timestamp: Long): List<ParsedBankEvent>
}
