# Security Test Plan

## P0 — Release blockers

### WEBVIEW-01 — Navigation isolation
Expected: WebView can render only appassets.androidplatform.net/assets/*.
Fail if an external URL can become the main document.

### WEBVIEW-02 — JavaScript bridge exposure
Expected: AndroidFinance is available only to trusted application content.
Fail if untrusted web content can invoke getTransactions, importSms, requestSmsPermission, or clearData.

### WEBVIEW-03 — XSS data extraction
Inject transaction-shaped strings containing HTML/script payloads.
Expected: payload is rendered as text and cannot execute.

### SMS-01 — Permission boundary
Run import without READ_SMS.
Expected: no SMS query and a permission error.

### SMS-02 — Malformed amount rejection
Test zero, negative, oversized and non-numeric amounts.
Expected: no Transaction is created.

### SMS-03 — Duplicate resistance
Import the same SMS twice.
Expected: exactly one transaction.

### STORAGE-01 — Corrupted ciphertext
Corrupt local encrypted storage.
Expected: application remains usable and does not crash.

### STORAGE-02 — Backup
Inspect APK/application backup configuration.
Expected: financial data is not included in Android backup.

## P1 — High priority

- Persian, Arabic and Latin digit normalization
- Rial/Toman conversion
- malformed JSON records
- missing SMS columns
- empty SMS body
- invalid timestamps
- low-confidence transactions marked for review
- raw SMS not returned through JavaScript
- card numbers limited to last four digits in UI data

## P2 — Release hardening

- dependency vulnerability review
- release APK static inspection
- exported component review
- debug build separation
- screenshot/recent-apps privacy review
- logcat scan for financial data
