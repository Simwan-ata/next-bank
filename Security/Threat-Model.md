# BankSmsManager Security Threat Model

## Assets
- Parsed financial transactions
- Card last-four digits
- Bank and merchant metadata
- Original SMS content stored locally
- Android Keystore encryption key

## Trust boundaries
1. Android SMS provider -> parser
2. Parser -> encrypted local repository
3. Native Kotlin -> WebView JavaScript bridge
4. Local WebView HTML/JavaScript -> UI

## P0 threats
- Untrusted WebView content invoking the JavaScript bridge
- XSS exposing transaction data through the bridge
- Unauthorized access to READ_SMS
- Backup/export of encrypted or raw financial data
- Parser accepting forged or malformed transaction amounts

## P1 threats
- Duplicate transaction insertion
- Numeric overflow
- Currency conversion errors
- Corrupted local JSON
- Permission revocation and partial import failures
- Sensitive data exposure through logs or UI

## Security invariants
- No network access is required for financial data processing.
- Raw SMS must never be returned to JavaScript unless a future feature explicitly requires it.
- Transaction amounts must be positive and fit in Long.
- Parser-generated IDs must be deterministic and collision-resistant.
- WebView navigation must remain inside the trusted local asset origin.
- Invalid stored records must be ignored rather than crashing the application.
- READ_SMS must be requested only when the user explicitly starts SMS import.
