# Belső API leírás

## Általános

A belső APi a legtöbb végponton JSON-t fogad el és azt is ad vissza.
A válaszba (ahol erre lehetőség van) értelmes és _kifejező_ HTTP státusz kód. A
válaszkódokról az [RFC-ben][1] lehet részletesebben olvasni.

Néhány fontosabb válaszkód:

* 200: sikeres végrehajtás
* 201: (POST kérés esetén) az entitást sikeresen létrehozva
* 202: (aszinkron művelet esetén) a feldolgozás elkezdődött, állapotjelentés a válasz törzsében
* 204: sikeres végrehajtás, nincs visszaküldendő adat

* 400: hibás kérés, ennek oka sokféle lehet. Rosszul formázott JSON-től
kezdve egészen érvénytelen adatokig.
* 404: a kért erőforrás (általában entitás) nem található

* 500: nagyjából minden más hiba, de próbáljuk meg ezt fenntartani az olyan hibák számára,
amikre nem tudunk felkészülni előre a backend oldalon

## Válasz formátum

A válaszban mindig JSON érkezik és a válaszban mindig van egy `success` mező, ami boolean értékű.

	{
		"success": true|false,
		...
	}

A `success` mező a HTTP statusz kóddal összhangban van, így 2XX-es kód esetén `true`,
minden más esetben `false` értékű.


## Sikeres kérés

Sikeres kérés esetén (amennyiben a válasz tartalmaz valamilyen adatot) a formátum a következő:

	{
		"success": true,
		"data": ...
	}

A `data` mező mindig a kérésre specifikus adatokat tartalmazza.

## Sikeretelen kérés

Sikertelen kérés esetén a válasznak mindig van törzse, még akkor is ha a back-end
oldalon váratlan hiba történik. A válaszban olyan információk találhatóak, amik alapján
a felhasználót már értelmesen lehet tájékoztatni arról, hogy mi is történt.

A visszaküldött json a következő struktúrával rendelkezik:

    {
        "success": false,
        "error_code": "XXXXX",
        "message": "rövid hibaüzenet"
        "details": {
            cause: "hiba pontosabb leírása, van hogy exception.getMessage()",
            ...
        }
    }

Az `error_code` mező az alkalmazásban egy előre definiált listából kerül ki. Ez
a kód leírja a hiba jellegét, például hogy validációs hiba történt vagy a kérés
törzsében küldött JSON hibás formátumú volt. A `message` a hibakód emberi
nyelvre fordítása, nem tartalmaz hiba kontextusához kapcsolódó változó
informácót. Végül pedig a `details` mező, ami a hibához esetlegesen kapcsolódó
információkat tartamaz. Ilyenek lehetnek egy-egy validációs hibánál, hogy mi is
hiányzik vagy rosszul megadott.

Az `error_code` a [PostgreSQL hibakód formátumát][2] veszi alapul. A hibakód
mindig egy **5** karakterből álló string. Az első két karakter a hiba osztályát
írja le, míg a második 3 karater a pontos hibakód. A teljes 5 karateres
hibakódok értelemszerűen egyediek. Az összes hibakód a PekErrorCode.java fájlban
találhatóak.

[1]: http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
[2]: http://www.postgresql.org/docs/9.3/static/errcodes-appendix.html

