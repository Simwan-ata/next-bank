package com.banksms.manager

/**
 * Applies deterministic local rules without network access or side effects.
 * Rules are evaluated by priority descending and id ascending.
 */
class FinancialRuleEngine(
    rules: List<FinancialRule>
) {
    private val rules = rules
        .filter { it.enabled }
        .sortedWith(compareByDescending<FinancialRule> { it.priority }.thenBy { it.id })

    fun preview(transactions: List<Transaction>): Map<String, Int> =
        transactions.mapNotNull { tx -> match(tx)?.let { it.id } }
            .groupingBy { it }
            .eachCount()

    fun apply(transactions: List<Transaction>): List<Transaction> =
        transactions.map { tx ->
            val rule = match(tx)
            if (rule == null) tx else tx.copy(category = rule.category)
        }

    private fun match(tx: Transaction): FinancialRule? =
        rules.firstOrNull { rule ->
            (rule.bank == null || rule.bank.equals(tx.bank, ignoreCase = true)) &&
            (rule.merchant == null || tx.description.contains(rule.merchant, ignoreCase = true)) &&
            (rule.type == null || rule.type.equals(tx.type, ignoreCase = true)) &&
            (rule.minAmount == null || tx.amount >= rule.minAmount) &&
            (rule.maxAmount == null || tx.amount <= rule.maxAmount)
        }
}
