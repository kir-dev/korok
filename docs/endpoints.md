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
