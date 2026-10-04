package com.banksms.manager

import org.junit.Assert.assertEquals
import org.junit.Test

class MerchantNormalizerTest {
    @Test
    fun longestAliasWins() {
        val normalizer = MerchantNormalizer(
            listOf(
                MerchantAlias("1", "فروشگاه", "فروشگاه عمومی"),
                MerchantAlias("2", "فروشگاه رفاه", "رفاه")
            )
        )

        assertEquals("رفاه", normalizer.normalize("فروشگاه رفاه"))
    }

    @Test
    fun unknownMerchantIsPreserved() {
        val normalizer = MerchantNormalizer()
        assertEquals("کافه", normalizer.normalize("کافه"))
    }
}
