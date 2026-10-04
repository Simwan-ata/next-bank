package com.banksms.manager

data class MerchantAlias(
    val id: String,
    val alias: String,
    val canonicalMerchant: String,
    val enabled: Boolean = true
)
