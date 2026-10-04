package com.banksms.manager

/**
 * Deterministic, local-only merchant normalization.
 * Alias precedence is explicit: enabled aliases are matched by longest alias,
 * then lexical order, so results never depend on collection iteration order.
 */
class MerchantNormalizer(
    aliases: List<MerchantAlias> = emptyList()
) {
    private val aliases = aliases
        .filter { it.enabled && it.alias.isNotBlank() && it.canonicalMerchant.isNotBlank() }
        .sortedWith(compareByDescending<MerchantAlias> { normalize(it.alias).length }.thenBy { normalize(it.alias) })

    fun normalize(raw: String): String {
        val value = raw.trim()
        if (value.isBlank()) return ""
        val normalized = normalize(value)
        val match = aliases.firstOrNull { normalized == normalize(it.alias) || normalized.contains(normalize(it.alias)) }
        return match?.canonicalMerchant?.trim().orEmpty().ifBlank { value }
    }

    private fun normalize(value: String): String =
        value.replace('ي', 'ی')
            .replace('ك', 'ک')
            .replace(Regex("\\s+"), " ")
            .trim()
            .lowercase()
}
