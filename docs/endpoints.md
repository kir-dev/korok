# API végpontok

Itt az api végpontokról lehet egy áttekintő leírást találni. Az egyes végpontokról
csak a legszükségesebb információkat találod meg itt, amivel már elindulhatsz.
Az api általános leírását a [api.md](api.md) fájlban találod.

Egyelőre minden végpont `/internal` prefixszel van ellátva. Ez még változhat a jövőben.
Az alábbi leírásból mindenhol elhagytuk a prefixet az átláthatóság miatt.

Ez tehát azt jelenti, hogy a leírásban megadott útvonal elé mindig oda kell illeszteni
a `/internal` prefixet, amikor a szerverrel akarunk kommunikálni.

Például egy felhasználó adatait a `/users/{virid}` útvonalon listázhatjuk, de valójában a
`/internal/users/{virid}` útvonalra kell a kérést küldeni.

Az egyes útvonalakban a {} közé tett kifejezések változók.

Nagyjából minden kérésre igaz, hogy a következő két headernek szerepelnie kell benne:

    Content-Type: application/json
    Accept: application/json

## Felhasználóhoz kapcsolódó végpontok

### Felhasználó adatainak listázása

    GET /users/{virid}

### Felhasználó tagságainak listázása

    GET /users/{virid}/memberships

### Felhasználó profil képe

Lekérdezés:

    GET /users/{virid}/avatar

Törlés:

    DELETE /users/{virid}/avatar

Új feltöltése (és ezzel együtt a régi törlése, ha van ilyen):

    PUT /users/{virid}/avatar

A kérés törzse tartalmazza magát a képet és a `Content-Type` header legyen helyesn
beállítva. Jeleneleg `image/jpeg`, `image/png` és `image/gif` mime-type-pal elátott
kéréseket fogadunk el.

### Felhasználó IM fiókjai (pl gtalk, skype stb)

Lekérdezés:

    GET /users/{virid}/im

    Példa válasz:
    {
        "id": 1,
        "protocol": "irc",
        "account_name": "tmichel"
    }


Új létrehozása:

    POST /users/{virid}/im

    {
        "protocol": "jabber"|"gtalk"|"skype"|"irc",
        "account_name": "tmichel"
    }

Törlés:

    DELETE /users/{virid}/im/{im_id}

## Keresés

    POST /search

A keresés jelenleg 2 módban használható: felhasználókat és köröket lehet keresni.
A keresés egy POST kéréssel lehet indítani. A következő formátumú JSON-t vár:

    {
        "term": "keresési kifejezés",
        "mode": "USER" | "GROUP",
        "page": <szám: alapértelmezetten 0>,
        "perPage": <szám: alapértelmezetten 25>
    }

A `term` és a `mode` kötelező mező. A lapozás 0. "lapról" indul.

A válaszban mindig megkapjuk, hogy hány kör és hány felhasználó van összesen az
adott keresési kifejezéssel. Ezen túl a megadott módtól függően a `users` vagy a
`groups` lista kerül feltöltésre.

Egy példa futtatás:

    $ curl localhost:8080/internal/search -i -H'Content-Type: application/json' -H'Accept: application/json' -d '{"term": "tmichel", "mode": "USER"}'
    HTTP/1.1 200 OK
    Connection: keep-alive
    X-Powered-By: Undertow/1
    Server: WildFly/8
    Transfer-Encoding: chunked
    Content-Type: application/json
    Date: Thu, 03 Jul 2014 10:52:12 GMT

    {
      "data" : {
        "count_of_users" : 1,
        "count_of_groups" : 0,
        "users" : [ {
          "email_address" : "tmichel@example.com",
          "first_name" : "Tamás",
          "last_name" : "Michelberger",
          "nick_name" : "Tomi",
          ...
        } ],
        "groups" : [ ]
      },
      "success" : true
    }

## Körök lekérdezése

### Összes kör lekérdezése

    GET /groups

Példa futtás

    $ curl localhost:8080/internal/groups -i -H'Content-Type: application/json' -H'Accept: application/json'

Ez visszaadja az összes aktív kör alapadatait. A `GroupView` osztályban találhatóak meg,
hogy pontosan milyen adatokat ad vissza.

### Egyetlen kör lekérdezése

    GET /groups/{id}

A megadott azonosítójú kört kérdezi le, annak alapadataival. A `GroupView` osztályban találhatóak meg,
hogy pontosan milyen adatokat ad vissza.

Egy példa:

    $ curl localhost:8080/internal/groups/106 -i -H'Content-Type: application/json' -H'Accept: application/json' -sHTTP/1.1 200 OK

    Connection: keep-alive
    X-Powered-By: Undertow/1
    Server: WildFly/8
    Transfer-Encoding: chunked
    Content-Type: application/json
    Date: Thu, 17 Jul 2014 15:20:04 GMT

    {
      "data" : {
        "introduction" : "A Villanykari Információs Rendszer fejlesztésével és üzemeltetésével foglalkozó kör.",
        "web_page" : "http://kir-dev.sch.bme.hu",
        "mailing_list" : "kir-dev@sch.bme.hu",
        "founded" : 2001,
        "is_svie" : true,
        "delegate_number" : 1,
        "users_can_apply" : true,
        "head" : "KIR Admin",
        "status" : "akt",
        "name" : "KIR fejlesztők és üzemeltetők",
        "id" : 106,
        "type" : "szakmai kör"
      },
      "success" : true
    }

### Körtagságok lekérdezése

    GET /groups/{id}/memberships/active

    GET /groups/{id}/memberships/inactive

`active` a jelenleg is aktív tagságokat adja vissza, míg az `inactive` az öregtagságokat.
Egy tömbként kapjuk meg a tagságokat. A tagságok a következő json objektumokként reprezentáltak.
Részletes leírás a `GroupMembershipView` osztályban található.

Egy példa:
    $ curl localhost:8080/internal/groups/106/memberships/inactive -i -H'Content-Type: application/json' -H'Accept: application/json' -s | head -30

    HTTP/1.1 200 OK
    Connection: keep-alive
    X-Powered-By: Undertow/1
    Server: WildFly/8
    Transfer-Encoding: chunked
    Content-Type: application/json
    Date: Thu, 17 Jul 2014 17:31:28 GMT

    {
      "data" : [ {
            "posts" : [ "öregtag" ],
            "user_fullname" : "John Smith",
            "end" : "2009-08-31",
            "start" : "2009-05-20",
            "id" : 1
          }, {
            "posts" : [ "öregtag" ],
            "user_fullname" : "John Doe",
            "end" : "2003-05-14",
            "start" : "2002-09-16",
            "id" : 2
          },
          ...
      ],
      success: true
    }
