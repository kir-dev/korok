# Profil és Körök (PÉK)

A [PÉK][1] a VIR lényegében utolsó megmaradt darabkája. A történetéről és
jövőjéről a [Kir-Dev blogon][2] lehet többet olvasni:
[I. rész][3], [II. rész][4], [III. rész][5] és [IV. rész][6].

A PÉK fejlesztése a [githubon][7] folyik. Amennyiben bugot fogsz az [issues][8]
oldalon van lehetőséged ezt felénk jelezni. Ha egyéb más problémád van (pl
regisztrálni szeretnél), akkor az Schönherz egységes [support oldalán][9] tudsz
ticketet feladni.

## Fejlesztőknek

A fejlesztői doksik a [`docs`](docs/) mappában találhatóak.

## Én is szeretnék a PÉK-be kódolni. Mit tegyek?

1. [Forkold a repot githubon](https://github.com/kir-dev/korok/fork)
2. Klónozd le magadhoz: `git clone https://github.com/your-nick/korok.git`
3. Rakj össze egy fejlesztői környezetet. Részletek az [install.md](docs/install.md)-ben.
4. Válassz magadnak feladatot: [issues oldalról][8] vagy kérdezz minket a [#kir-dev irc csatornán a freenodeon][10]
5. Dolgozz egy külön branchen: `git checkout -b my-awesome-patch`
6. Lehetőleg írj tesztet és futtasd le a már meglévőeket: `mvn clean test`
6. Pushold a kódod a saját repótba: `git push -u origin my-awesome-patch`
8. Küldj [pull requestet](https://github.com/kir-dev/korok/pulls).

## Kérdésem van, mit tegyek?

Elérsz minket a kir-dev [kukac] sch.bme.hu email címen, de a [#kir-dev IRC csatornán][10]
általában gyorsabban kapsz választ.

[1]: https://korok.sch.bme.hu
[2]: http://kir-dev.sch.bme.hu
[3]: http://kir-dev.sch.bme.hu/pek/2014/01/23/pek-jelen-es-jovo-i/
[4]: http://kir-dev.sch.bme.hu/pek/2014/01/25/pek-jelen-es-jovo-ii/
[5]: http://kir-dev.sch.bme.hu/pek/2014/01/27/pek-jelen-es-jovo-iii/
[6]: http://kir-dev.sch.bme.hu/pek/2014/01/29/pek-jelen-es-jovo-iv/
[7]: https://github.com/kir-dev/korok
[8]: https://github.com/kir-dev/korok/issues
[9]: http://support.sch.bme.hu/
[10]: http://webchat.freenode.net/?channels=kir-dev
