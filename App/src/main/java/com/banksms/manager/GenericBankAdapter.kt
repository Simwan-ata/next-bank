package com.banksms.manager

/**
 * Fallback adapter for SMS formats not yet covered by a bank-specific adapter.
 * It delegates to the canonical event parser and preserves unknown/review events.
 */
class GenericBankAdapter : BankAdapter {
    override val bankId: String = "generic"

    override fun canHandle(sender: String?, body: String?): Boolean =
        !body.isNullOrBlank()

    override fun parse(sender: String?, body: String?, timestamp: Long): List<ParsedBankEvent> =
        BankSmsEventParser.parse(sender, body, timestamp)
}
