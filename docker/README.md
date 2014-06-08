# PÉK docker image-k

Az itt található szkriptek segitségével egyszerűen elkeszítheted a fejlesztőkörnyezetet a PÉK-hez.

Továbbra is szükseged lesz linuxra vagy OSX-re a fejlesztéshez, de ez VM is lehet. Telepítsd fel a [docker][1]-t, ubuntu esetén pl. így:

    $ sudo apt-get update
    $ sudo apt-get install linux-image-extra-`uname -r`
    $ sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
    $ sudo sh -c "echo deb http://get.docker.io/ubuntu docker main\
    > /etc/apt/sources.list.d/docker.list"
    $ sudo apt-get update
    $ sudo apt-get install lxc-docker

Ajánlott elolvasni a teljes [telepítesi doksit][2]. 14.04-tol is javasolt a fenti módszer, mert ugyan benne van már az ubuntus repoban, de csak karbantártasi releaseket kapsz onnan, azt is lassan.

## Let's do it

    $ cd ~/korok/docker/data/
    $ cp -r ~/korok/resources appdata # modify the config if needed
    $ cp ~/vir-dump.sql appdata/ # db dump or dummy db
    $ cd ~/korok/docker
    $ ./build-images.sh
    $ ./create-instances.sh

## Mit is csinálnak a fentiek?

PostgreSQL-t és Wildfly-t telepítenek és konfigurálnak egymástól és a host-tól elszeparált konténerekben.

A jelenlegi setup 3 konténerből áll:
* **data**: *shared data volume*
    - ebben vannak az app resource valamint az adatbázis data fájlok
    - ha törlöd a konténert, akkor az ott módositott / létrehozott fájlok értelemszerűen elvesznek
    - a többi konténer törlese az ebben tárolt adatokat nem befolyásolja
    - ha nagyon akarod a host gépedre is linkelhetsz, lásd ismet a [doksit][3]
    - ennek nem kell futnia, csak egyszer le kell buildelni es utána egyszer `run`-t indítani rá, hogy elnevezzük (csak a ps -a kimenettel látszik, *exited* státuszban)
* **postgres**
    - ha nem létezik még a /var/lib/postgresql mappa, akkor inicializálja a `vir` es `vir-test` adatbázisokat és a `kir` felhasználót
    - az egyszerűség kedvéért a host gépre ki van forwardolva a fix 5432-es porton
    - `create-instances.sh` fájlban tudod módosítani a jelszót
* **wildfly**: appszerver
    - portok: 8080 (web), 9990 (cli), 22 (ssh)
    - be tudsz lépni ssh-val (root/dev123)
    - innen tudod listázni és szerkeszteni a data konténerben lévő dolgokat is
    - jelenleg nincsenek a portok fixre forwardolva, mert ez a javasolt beállítás

## Docker 101

Három lényeges fogalom van:
- **Dockerfile**: egy recipe / szkriptgyűjtemény, amiből tudsz `build`-elni egy image-t (sablont)
- **image**: egy kész környezet sablonja. Ebből példányosíthatsz `run` paranccsal egy teljesen új konténer példányt
- **container**: egy példányosított image, egy image-ből bármennyi konténer példányt tudsz készíteni, ezeket tudod leállítani, elindítani

Az alábbiaknál a névhez mindig konténer nevét kell írni.

Portforward lekérdezése: `docker port <NAME> <PORT>`  
Konténer leállítása, elindítása: `docker [stop|start] <NAME>`  
Futó konténerek: `docker ps`  
Logok figyelése: `docker logs -f <NAME>`

## PÉK install

Ugyanúgy, ahogy eddig, csak a cli portját a Wildfly konténerre állítsd be.

[1]: https://www.docker.io/
[2]: http://docs.docker.io/installation/ubuntulinux/#
[3]: http://docs.docker.io/userguide/dockervolumes/
