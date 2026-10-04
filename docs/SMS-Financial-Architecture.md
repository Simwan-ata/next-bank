# SMS Financial Architecture

## Product boundary

The application treats bank SMS as a local financial data source, not merely as a transaction feed. A single SMS may contain one or more facts: transaction, reported balance, transfer metadata, merchant, counterparty, fee, bill/recharge/installment information, reference identifiers, or an unknown event requiring review.

## Canonical flow

`SMS source → BankAdapter → ParsedBankEvent → domain projection → analysis/reporting`

`BalanceObservation` is a separate domain object. A reported bank balance is evidence from the bank and must never be fabricated as a transaction.

## Adapter contract

- Each bank-specific adapter is deterministic and local-only.
- `BankAdapter.canHandle()` identifies the format without network calls.
- `BankAdapter.parse()` returns canonical events rather than directly deciding UI categories.
- `GenericBankAdapter` is a fallback and must produce lower-confidence/reviewable results when the format is uncertain.
- Bank-specific adapters must have dedicated fixtures/tests before being promoted.

## Data principles

- Keep original SMS available only in the protected native data layer.
- Preserve provenance: source message, parser/adapter identity, confidence and review state.
- Keep event identity deterministic so repeated SMS imports are idempotent.
- Do not infer a transaction merely because a balance changed.
- Currency and Rial/Toman conversion must be explicit.
- Persian, Arabic and Latin digits and common separators must normalize consistently.

## Planned domain capabilities

1. Merchant normalization and editable aliases.
2. Deterministic rule engine with explicit priority, preview and provenance.
3. Balance reconciliation and gap detection.
4. Transfer/counterparty modeling.
5. Recurring, installment, bill and recharge detection.
6. Comparative reporting and Persian-calendar presentation.
7. Local search and aggregation without decrypting the entire sensitive dataset.
8. Data-quality and unknown-message review queues.

## Security boundary

Financial data remains on-device. No cloud parser, remote analytics, telemetry SDK, or network enrichment is part of the architecture.