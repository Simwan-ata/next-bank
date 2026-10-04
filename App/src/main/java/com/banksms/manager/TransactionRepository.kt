package com.banksms.manager

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class TransactionRepository(context: Context) {
    private val store = SecureStore(context)
    private val key = "transactions"

    fun all(): MutableList<Transaction> {
        val raw = store.get(key, "[]")
        val array = runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
        val result = mutableListOf<Transaction>()
        for (i in 0 until array.length()) {
            val o = runCatching { array.getJSONObject(i) }.getOrNull() ?: continue
            val amount = o.optLong("amount", -1L)
            if (amount <= 0L) continue
            result += Transaction(
                id = o.optString("id"),
                timestamp = o.optLong("timestamp"),
                type = o.optString("type"),
                amount = amount,
                description = o.optString("description"),
                bank = o.optString("bank"),
                cardLast4 = o.optString("cardLast4"),
                person = o.optString("person"),
                category = o.optString("category"),
                source = o.optString("source"),
                confidence = o.optInt("confidence"),
                needsReview = o.optBoolean("needsReview"),
                raw = o.optString("raw")
            )
        }
        return result
    }

    fun addAll(items: List<Transaction>): Int {
        val existing = all()
        val ids = existing.map { it.id }.toMutableSet()
        var added = 0
        for (item in items) {
            if (item.amount <= 0L || item.timestamp <= 0L) continue
            if (item.id.isBlank()) continue
            if (ids.add(item.id)) {
                existing += item
                added++
            }
        }
        save(existing.sortedByDescending { it.timestamp }.take(5000))
        return added
    }

    fun clear() = store.put(key, "[]")

    private fun save(items: List<Transaction>) {
        val array = JSONArray()
        items.forEach { t ->
            array.put(JSONObject().apply {
                put("id", t.id)
                put("timestamp", t.timestamp)
                put("type", t.type)
                put("amount", t.amount)
                put("description", t.description)
                put("bank", t.bank)
                put("cardLast4", t.cardLast4)
                put("person", t.person)
                put("category", t.category)
                put("source", t.source)
                put("confidence", t.confidence)
                put("needsReview", t.needsReview)
                put("raw", t.raw)
            })
        }
        store.put(key, array.toString())
    }
}