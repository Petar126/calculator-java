# TEST-RESULTS

Sistemsko testiranje po metodi crne kutije (black box). Kalkulatoru su zadavani različiti izrazi i poređen je dobijeni rezultat sa očekivanim.
Dodatno je napisan jedan jedinični test (`CalculatorTest.java`).

## Šta radi ispravno

- Osnovne operacije `+`, `-`, `*`, `/` (npr. `4+5` daje `9.0`).
- Prioritet operacija (npr. `10+5*4+3` daje `33.0`).
- Izrazi sa više operacija istog prioriteta (npr. `10-5-2`, `100/10/5`).
- Decimalni brojevi sa tačkom, negativan broj na početku izraza i razmaci u izrazu.

## Pronađeni propusti i nedostaci

1. **Prazan unos** ruši program (`StringIndexOutOfBoundsException`).
2. **Nepotpun izraz** se prihvata kao ispravan: `5+`, `5*` i `1-` vraćaju `5.0` i `1.0`, a sam `+` ili `-` vraća `0.0` umesto greške.
3. **Negativan broj iza operatora** nije podržan: `2*-3` i `5+-3` vraćaju `ERROR`.
4. **Deljenje nulom** nije obrađeno: `5/0` daje `Infinity`, a `0/0` daje `NaN`.
5. **Preciznost**: zbog tipa `float` veliki brojevi daju netačan rezultat (npr. `16777216+1` daje `1.6777216E7`, a `99999999*99999999` daje `1.0E16`).
6. **Prikaz rezultata**: veliki i mali rezultati se ispisuju u naučnoj notaciji (`1.0E-12`), a celi brojevi uvek sa `.0` (`9.0`).
7. **Nedosledno ponašanje kod naučne notacije**: `1e3+1` radi, a `1E+3` vraća `ERROR`.
8. **Neispravni ulazi se prihvataju**: reči `Infinity` i `NaN` tretiraju se kao brojevi (`NaN+1` daje `NaN`).
9. **Nepodržane funkcije**: zagrade (`(2+3)*4`), stepenovanje (`2^3`) i ostatak (`5%2`) vraćaju `ERROR`. Decimalni zarez (`1,5+2`) takođe vraća `ERROR`.
10. **Poruka o grešci** je uvek samo `ERROR`, bez objašnjenja šta je pogrešno (npr. za `abc`, `2++3`, `2**3`).

## Jedinični test

`CalculatorTest.java` sadrži jedan JUnit test koji proverava metodu za računanje (`Calculate`, pozvanu preko `Calculator.Run`).
Izraz `10+5*4+3` treba da vrati `33.0`, čime se proverava prioritet operacija. Test prolazi.
