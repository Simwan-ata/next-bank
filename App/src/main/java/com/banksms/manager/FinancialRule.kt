package com.banksms.manager

/**
 * Local deterministic rule definition.
 * Priority is explicit so rule application never depends on map/set iteration order.
 */
data class FinancialRule(
    val id: String,
    val name: String,
    val priority: Int,
    val enabled: Boolean = true,
    val bank: String? = null,
    val merchant: String? = null,
    val type: String? = null,
    val minAmount: Long? = null,
    val maxAmount: Long? = null,
    val category: String,
    val stopAfterMatch: Boolean = true
)
