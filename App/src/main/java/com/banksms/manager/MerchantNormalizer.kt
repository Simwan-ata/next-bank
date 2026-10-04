package com.banksms.manager

/**
 * Deterministic, local-only merchant normalization.
 * Alias precedence is explicit: longest normalized alias, then lexical order.
 */
class MerchantNormalizer(
    aliases: List<MerchantAlias> = emptyList()
) {
    private val aliases = aliases
        .filter { it.enabled && it.alias.isNotBlank() && it.canonicalMerchant.isNotBlank() }
        .sortedWith(compareByDescending<MerchantAlias> { normalizeKey(it.alias).length }.thenBy { normalizeKey(it.alias) })

    fun normalize(raw: String): String {
        val value = raw.trim()
        if (value.isBlank()) return ""
        val normalized = normalizeKey(value)
        val match = aliases.firstOrNull {
            normalized == normalizeKey(it.alias) || normalized.contains(normalizeKey(it.alias))
        }
        return match?.canonicalMerchant?.trim().orEmpty().ifBlank { value }
    }

    private fun normalizeKey(value: String): String =
        value.replace('ي', 'ی')
            .replace('ك', 'ک')
            .replace(Regex("\\s+"), " ")
            .trim()
            .lowercase()
}
