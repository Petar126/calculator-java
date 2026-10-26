# calculator-java

Jednostavan konzolni kalkulator u Javi (fork repozitorijuma
[vladimir-dresevic/calculator-java](https://github.com/vladimir-dresevic/calculator-java)).

Unosi se aritmetički izraz sa operacijama `+`, `-`, `*`, `/` (npr. `2+3*4`), a program ispisuje rezultat.
Za izlaz se unosi `exit`.

---

# Izveštaj: softverska metrika i statička analiza koda

## 1. Metrika LOC (Lines of Code)

| Fajl            | Ukupno linija (fizički LOC) | Prazne linije | Linije komentara | Linije koda (bez praznih i komentara) |
|-----------------|----------------------------:|--------------:|-----------------:|--------------------------------------:|
| Calculator.java | 188                         | 54            | 5                | 129                                   |
| Start.java      | 26                          | 7             | 0                | 19                                    |
| **Ukupno**      | **214**                     | **61**        | **5**            | **148**                               |

**LOC za kompletan projekat: 214 fizičkih linija, od toga 148 linija izvršnog/deklarativnog koda.**

Linije su prebrojane brojanjem redova u fajlovima (`awk`/`grep`). Prazna linija je linija koja sadrži samo
razmake ili tabove, a linija komentara je linija koja počinje sa `//`.

## 2. Neformalni pregled koda i statička analiza

Statička analiza je urađena bez pokretanja programa:

- kompajler kao lint alat: `javac -Xlint:all Calculator.java Start.java`. Kompajler nije prijavio
  nijednu grešku ni upozorenje, pa je kod sintaksno ispravan;
- ručni pregled koda (code review), gde sam tražio nedoslednosti, propuste i sumnjive delove koda (code smell).

Zapažanja su data u formatu **fajl – broj linije koda – zapažanje**.

### Calculator.java

- **Calculator.java – 6** – Rezultat se čuva u statičkom polju `finalResult` (globalno promenljivo stanje).
  Metoda `Calculate` ne vraća vrednost nego menja ovo polje, pa kod nije bezbedan za rad sa više niti
  i teško se testira. Bolje je da `Calculate` vrati `float`.
- **Calculator.java – 6, 51, 62** – Koristi se tip `float`, koji ima malu preciznost (npr. `0.1+0.2` ne daje
  tačno `0.3`, a veliki brojevi gube cifre). Za kalkulator je bolji `double` ili `BigDecimal`.
- **Calculator.java – 18** – Metoda se zove `ToString()` (statička, sa velikim slovom). To je u suprotnosti
  sa Java konvencijom imenovanja i lako se meša sa standardnom metodom `toString()`.
- **Calculator.java – 19** – Redosled simbola `+*/-` je bitan jer se string koristi u regularnom izrazu
  `[...]` (linija 37). Kada bi se `-` našao između dva znaka, tumačio bi se kao opseg znakova. Ova zavisnost
  je krhka i nigde nije dokumentovana. Bolje je koristiti `Pattern.quote` ili eksplicitno escape-ovati `-`.
- **Calculator.java – 24, 74** – Imena metoda `Run` i `Calculate` počinju velikim slovom, što je u
  suprotnosti sa Java konvencijom (camelCase: `run`, `calculate`). U istoj klasi metoda `evaluateExpression`
  poštuje konvenciju, pa je imenovanje nedosledno.
- **Calculator.java – 24–26** – Metoda `Run` samo prosleđuje poziv metodi `evaluateExpression` i nema
  dodatnu logiku (nepotreban sloj, "middle man").
- **Calculator.java – 32** – Nema provere da li je ulaz prazan ili `null`. Za prazan string
  `expression.charAt(0)` baca `StringIndexOutOfBoundsException` i program se ruši (u `Start.java` se
  prazan red prosleđuje direktno).
- **Calculator.java – 34** – `0 + expression` se oslanja na implicitnu konverziju `int` u `String`.
  Jasnije je napisati `"0" + expression`.
- **Calculator.java – 37** – Izraz se deli po svim operatorima, pa negativni brojevi posle operatora nisu
  podržani: `2*-3` daje prazan string između operatora, `parseFloat("")` baca izuzetak i rezultat je
  `ERROR`. Isto se dešava sa naučnom notacijom (`1E+5`).
- **Calculator.java – 41** – Petlja ide do `expression.length() - 1`, pa se poslednji znak ne proverava.
  Zajedno sa ponašanjem `split` metode, koja odbacuje prazne stringove na kraju, izraz sa operatorom na
  kraju (npr. `5+`) ne prijavljuje grešku nego tiho vraća `5.0`. Neispravan unos nije validiran.
- **Calculator.java – 42–45** – Duga `||` provera za sve operatore duplira listu simbola koja već postoji
  u `Operations.ToString()`. Jednostavnije je `Operations.ToString().indexOf(c) != -1`.
- **Calculator.java – 53** – Klasična `for` petlja sa indeksom može da se zameni `for-each` petljom
  (`for (String number : numbers)`).
- **Calculator.java – 55–56** – Grana za `"-Infinity"` je mrtav kod (dead code). Znak `-` je već
  iskorišćen kao separator pri `split` metodi (linija 37), pa nijedan element niza `numbers` nikada ne može
  da počne sa `-`.
- **Calculator.java – 57–58** – Posebna obrada za `"Infinity"` nije potrebna, jer `Float.parseFloat`
  već ume da parsira `"Infinity"`.
- **Calculator.java – 63** – Hvata se opšti `Exception` umesto konkretnog `NumberFormatException`, pa se
  mogu "progutati" i greške koje nisu očekivane.
- **Calculator.java – 64** – Greška se vraća kao magični string `"ERROR"`, bez poruke o tome šta nije u
  redu. Bolje je baciti izuzetak ili koristiti konstantu i opisnu poruku.
- **Calculator.java – 70–71** – Nepotrebna privremena promenljiva `textResult`. Može direktno
  `return Float.toString(finalResult);`.
- **Calculator.java – 74–186** – Metoda `Calculate` je predugačka (oko 110 linija) i ima visoku
  ciklomatsku složenost (code smell "Long Method").
- **Calculator.java – 81, 88, 98, 111, 123, 139, 149, 163, 175** – `result` se inicijalizuje na `0`, a zatim
  se koristi `result +=`, iako se u svakom pozivu računa samo jedna operacija. Dovoljno je dodeliti
  (`result = ...`). Sabiranje je ovde suvišno i zbunjuje čitaoca.
- **Calculator.java – 86–184** – Dupliran kod (code smell "Duplicated Code"). Isti blok od 5 naredbi
  (izračunaj, `set`, `remove`, `remove`, rekurzivni poziv) ponavlja se 8 puta, praktično identičan, za
  `*`, `/`, `+` i `-`. Treba ga izdvojiti u pomoćnu metodu, npr.
  `applyOperation(numbers, operations, index)`.
- **Calculator.java – 86–132** – Logika "nađi prvi `*` ili `/`" je napisana preko tri grananja. Isti efekat
  se postiže jednim prolazom kroz listu operacija, gde se uzima prvi operator višeg prioriteta. Isto važi za
  `+` i `-` (linije 134–184).
- **Calculator.java – 95, 105, 118, 130, 146, 156, 170, 182** – Rekurzija se koristi za ono što je
  prirodno iterativno (petlja dok ima operacija). Za veoma dug izraz može doći do `StackOverflowError`.
- **Calculator.java – 98, 123** – Nema provere deljenja nulom. `5/0` vraća `Infinity`, a `0/0` vraća `NaN`,
  umesto jasne poruke o grešci.
- **Calculator.java – 160** – Suvišna prazna linija ispred zatvorene zagrade bloka (nedosledno
  formatiranje u odnosu na ostale blokove).
- **Calculator.java – 186** – Ako lista operacija nije prazna, a ne sadrži nijedan poznati operator, metoda
  se završava bez postavljanja `finalResult`, pa ostaje rezultat **prethodnog** izraza (posledica globalnog
  stanja iz linije 6).
- **Calculator.java – 188** – Fajl se ne završava znakom za novi red (newline at end of file).
- **Calculator.java – 1–4** – Klasa nije u paketu (`package` deklaracija ne postoji). Ne postoji ni JavaDoc
  dokumentacija klase i javnih metoda.

### Start.java

- **Start.java – 1–3** – Klasa nije u paketu (default package), isto kao `Calculator`.
- **Start.java – 5–24** – Fajl je uvučen tabovima, a `Calculator.java` razmacima. Formatiranje u projektu
  je nedosledno.
- **Start.java – 6** – Lokalna promenljiva `Expression` počinje velikim slovom, što je u suprotnosti sa
  Java konvencijom (`expression`). Deklarisana je daleko od mesta upotrebe.
- **Start.java – 7, 11, 17** – Promenljiva `active` se koristi kao zastavica za izlaz iz petlje. Isto je
  čitljivije sa `while (true)` i `break`.
- **Start.java – 10** – Linija sadrži samo tab (trailing whitespace).
- **Start.java – 12** – Novi `Scanner` nad `System.in` se pravi u **svakoj iteraciji** petlje. Prethodni se
  nikada ne zatvara (curenje resursa, resource leak). `Scanner` treba napraviti jednom, pre petlje, najbolje
  u `try-with-resources` bloku.
- **Start.java – 13** – Nema obrade situacije kada ulaz ne postoji (EOF, npr. Ctrl+D / Ctrl+Z):
  `nextLine()` tada baca `NoSuchElementException`.
- **Start.java – 15** – Poređenje sa `"exit"` je osetljivo na velika i mala slova i na razmake
  (`"Exit"` ili `"exit "` se ne prepoznaju). Bolje je `Expression.trim().equalsIgnoreCase("exit")`.
  Komanda `exit` je i magični string.
- **Start.java – 16** – Zatvaranje `Scanner`-a zatvara i `System.in`. Ovde je to slučajno bezbedno jer
  program odmah završava, ali je krhko.
- **Start.java – 19** – Ulaz se prosleđuje kalkulatoru bez ikakve provere (npr. prazan red), pa se program
  ruši zbog greške opisane u `Calculator.java – 32`.

### Opšta zapažanja o projektu

- Ne postoje jedinični testovi (npr. JUnit), pa se ispravnost izračunavanja ne može automatski proveriti.
- Ne postoji alat za build (Maven/Gradle) ni konfiguracija za lint/formatiranje (Checkstyle, PMD).
- Nisu podržane zagrade ni unarni minus posle operatora, a takav unos se ne prijavljuje korisniku kao
  nepodržan.
