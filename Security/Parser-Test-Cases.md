# SMS Parser Security Cases

| ID | Input condition | Expected |
|---|---|---|
| P-01 | Empty body | null |
| P-02 | null body | null |
| P-03 | timestamp <= 0 | null |
| P-04 | amount = 0 | null |
| P-05 | negative amount | null |
| P-06 | amount > Long.MAX_VALUE | null |
| P-07 | valid Persian digits | accepted |
| P-08 | valid Arabic digits | accepted |
| P-09 | valid Latin digits | accepted |
| P-10 | Toman amount | converted to Rial |
| P-11 | balance-only SMS | rejected |
| P-12 | duplicate SMS | same deterministic ID |
| P-13 | bank + card + merchant | high confidence |
| P-14 | incomplete transaction | low confidence / review |
| P-15 | HTML/script in merchant | must never execute in UI |
