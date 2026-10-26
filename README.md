# calculator-java

## LOC

| Fajl            | LOC     |
|-----------------|--------:|
| Calculator.java | 188     |
| Start.java      | 26      |
| **Ukupno**      | **214** |

## Izveštaj (fajl - broj linije koda - zapažanje)

- Calculator.java - 6 - Rezultat se čuva u statičkom polju `finalResult` (globalno stanje), umesto da ga metoda vrati.
- Calculator.java - 18 - Metoda `ToString()` ne poštuje Java konvenciju imenovanja i lako se meša sa `toString()`.
- Calculator.java - 24, 74 - Imena metoda `Run` i `Calculate` počinju velikim slovom (treba `run`, `calculate`).
- Calculator.java - 32 - Nema provere praznog unosa, pa `charAt(0)` baca izuzetak.
- Calculator.java - 37 - Negativan broj posle operatora (npr. `2*-3`) nije podržan i vraća `ERROR`.
- Calculator.java - 41 - Petlja ne proverava poslednji znak, pa izraz `5+` ne javlja grešku.
- Calculator.java - 55 - Provera za `"-Infinity"` je mrtav kod, jer je `-` već uklonjen pri `split` metodi.
- Calculator.java - 63 - Hvata se opšti `Exception` umesto `NumberFormatException`.
- Calculator.java - 81 - `result += ...` je nepotrebno, dovoljno je `result = ...`.
- Calculator.java - 86-184 - Isti blok koda se ponavlja 8 puta (dupliran kod), a metoda `Calculate` je predugačka.
- Calculator.java - 98, 123 - Nema provere deljenja nulom.
- Start.java - 6 - Promenljiva `Expression` počinje velikim slovom (treba `expression`).
- Start.java - 12 - Novi `Scanner` se pravi u svakoj iteraciji petlje, a prethodni se ne zatvara (curenje resursa).
- Start.java - 15 - Poređenje sa `"exit"` je osetljivo na velika slova i razmake.
