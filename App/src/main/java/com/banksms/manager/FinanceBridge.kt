package com.banksms.manager

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.provider.Telephony
import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONObject

class FinanceBridge(
    private val activity: Activity,
    private val repository: TransactionRepository
) {
    @JavascriptInterface
    fun getTransactions(): String {
        val array = JSONArray()
        repository.all().forEach { t ->
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
            })
        }
        return array.toString()
    }

    @JavascriptInterface
    fun hasSmsPermission(): Boolean =
        activity.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED

    @JavascriptInterface
    fun requestSmsPermission() {
        activity.runOnUiThread {
            if (!hasSmsPermission()) {
                activity.requestPermissions(arrayOf(Manifest.permission.READ_SMS), 4101)
            }
        }
    }

    @JavascriptInterface
    fun importSms(): String {
        if (!hasSmsPermission()) {
            return JSONObject().put("ok", false).put("error", "permission").toString()
        }

        val cursor = activity.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            null,
            null,
            Telephony.Sms.DATE + " DESC"
        ) ?: return JSONObject().put("ok", false).put("error", "read_failed").toString()

        val parsed = mutableListOf<Transaction>()
        var scanned = 0
        var recognized = 0

        cursor.use {
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)

            while (it.moveToNext() && scanned < 5000) {
                scanned++
                val address = if (addressIndex >= 0) it.getString(addressIndex) else ""
                val body = if (bodyIndex >= 0) it.getString(bodyIndex) else ""
                val date = if (dateIndex >= 0) it.getLong(dateIndex) else 0L
                SmsTransactionParser.parse(address, body, date)?.let {
                    parsed += it
                    recognized++
                }
            }
        }

        val added = repository.addAll(parsed)
        return JSONObject()
            .put("ok", true)
            .put("scanned", scanned)
            .put("recognized", recognized)
            .put("added", added)
            .toString()
    }

    @JavascriptInterface
    fun clearData(): String {
        repository.clear()
        return JSONObject().put("ok", true).toString()
    }
}