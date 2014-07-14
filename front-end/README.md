# Profil és Körök Front-End

A PÉK front-end node.js-ben lett újra írva. A fejlesztése a githubon folyik,
amennyiben bugot találsz az issues oldalon van lehetőséged ezt felénk jelezni.
A front-end-el kapcsolatos issuekat jelöljétek meg `frontend - ` prefixszel
például: frontend - authentikációs hiba.

## Fejlesztőknek

A PÉK front-end a repon belül a front-end mappában található. Amennyiben
szeretnél fejleszteni szükséges, hogy az alábbi lépéseket végrehajtsd:

1. Forkold a repot githubon
2. Klónozd le magadhoz: `git clone https://github.com/your-nick/korok.git`
3. Telepítsd a node.js-t [innen](https://www.nodejs.org/) a leírtaknak megfelelően!
4. Válassz egy feladatot az issues oldalról vagy kérdezz minket a #kir-dev irc csatornán.
5. Dolgozz egy külön branchen: `git checkout -b my-new-awesome-feature`
6. Pushold a kódod a saját repodba: `git push -u origin my-new-awesome-feature`
7. Küldj pull requestet

## A front-end indítása

1. Lépj a front-end mappájába: `cd /repos/korok/front-end`
2. Add ki az `npm install` parancsot, ez a `packages.json`-ban megadott összes függőséget telepíteni fogja
3. A `config.example.js` fájlban ki kell töltened a konfigurációs beállításokat, ehhez keress meg minket a #kir-dev irc csatornán!
3. A `node app.js` paranccsal tudod futtatni a front-end alkalmazást

## Gyors áttekintés a projektről

* `/app.js`: az alkalmazás elindításához és inicializálásához szükséges kódokat tartalmazza

    Ebben a fájlban található meg az [express.js](http://expressjs.com/) keretrendszer konfigurációja, a routing és
    az alkalmazás futtatása.

* `/packages.json`: az alkalmazás futásához szükséges függőségek, az `npm install` paranccsal telepíthetők
* `/config.example.js`: ez egy példa konfigurációs fájl, másold le `config.js` néven és tölds ki megfelelően
* `/routes/`: a routeok implementációja

    Ebben a könyvtárban található meg a route-okat megvalósító modulok

* `/public/`: a statikusan kiszolgált scriptek, képek és részleges html fájlok helye
* `/public/scripts`: a kliens oldali javascriptek helye

    A kliens oldalon [angular.js](https://angularjs.org/) keretrendszert használunk a megjelenítéshez
    A scriptek három kategóriába sorolhatók: `controllers`, `directives`, `services`
    A gyökérben található az angular-app inicializálása

* `/public/stylesheets`: itt találhatóak a css fájlok

    A stíluslapokat LESS-ben írjuk, amiből aztan css fájlokat generálunk.
    A LESS használatáról részletesen [itt](http://lesscss.org/) olvashattok

* `/public/partials`: a részleges html fájlok itt találhatóak

    A részleges html fájlokat az Angular.js segítségével include-oljuk az oldalakba.
    Az ngInclude dokumentációja [itt](https://docs.angularjs.org/api/ng/directive/ngInclude) érhető el

* `/views/`: A routeokhoz tartozó ejs fájlokat találhatod itt, ezek adják a keretet amiben a részleges html fájlokat includeoljuk

    A template engine az ejs melyről [itt](http://embeddedjs.com/) olvashatsz többet

* `/utils/`: a különböző segéd modulokat tároljuk itt




