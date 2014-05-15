# PÉK telepítési útmutató

Ebben a doksiban a fejlesztői környezet összállításához szükséges információkat
találod. A telepítéshez szükséges információk valószínűleg a
[Kir-Dev redmine-on](https://redmine.kirdev.sch.bme.hu) találhatóak.

## Back-end (internal-api)

### Előkövetelmények

* [PostgreSQL][1] legújabb változata + hozzá való [JDBC4 driver][2]
* JBoss AS 7
    * Szerezd be a szervert. Figyelj arra, hogy nem biztos, hogy binárisan
      letölthető a legújabb stabil verzió, ebben az esetben töltsd le a forrást
      és fordítsd le (maven powa). A lefordított 7.1.3-as verziót megtalálod itt:
      `stewie://home/balo/jboss-v7.1.3.zip`
* virdb dump (keresd @tmichel-t)
* körök konfigurációs mappa megléte
* Git
* Maven
* JDK (1.7+)

### PostgreSQL konfig

Elérhető egy teszt adatbázis _stewie-n_. Hozzáférést @tmichel-nél vagy
@salierri-nél tudsz kérni.

### Adatbázis és felhasználó létrehozása

    su - postgres
    createuser -l -E -P -R -d -S kir #Adjuk meg neki a jelszót
    createdb -O kir -E utf8 vir

Ha gondok vannak a használattal, pl nem enged be kir felhasználóként:

Keresd meg a PostgreSQL `pg_hba.conf` fájlját, menj az aljára és módosítsd a
Local host (IPv4)-nél a sor végét (pl. ident sameuser-ről) md5-re, majd indítsd
újra a szervert.

    local all all md5

Dump betöltése:

    psql -U kir -d vir -h localhost < dump.sql

Az adatbázis helyes működését ellenőrizheted a `psql -U kir -d vir -h localhost`
paranccsal kapott konzolban.

### JBoss konfig

A szervert jelenleg standalone módban használjuk. A `jboss-cli` használatához
értelemszerűen futnia kell a szervernek (hasznosság: [cli recipes][3]). A cli-hez
tudni kell, hogy van faja tab kiegészítés és van egy fajta fura szintaxisa
(`:`,`/`,`()`,`=` random használata), de meg lehet szokni.

#### Postgres modul telepítése

* Állítsd le a JBoss szervert.
* Töltsd le a legújabb [JDBC4 drivert][2].
* Hozd létre a következő könyvtárstruktúrát a szerver mappájában:

        modules/org/postgresql/main

* A fenti mappában hozz létre egy `module.xml` nevű fájlt.
* A tartalma legyen a következő (verzió értelemszerűen javítandó):

        <?xml version="1.0" encoding="UTF-8"?>
        <module xmlns="urn:jboss:module:1.0" name="org.postgresql">
            <resources>
                <resource-root path="postgresql-Y.X-Z.jdbc4.jar"/>
            </resources>
            <dependencies>
                <module name="javax.api"/>
                <module name="javax.transaction.api"/>
            </dependencies>
        </module>

* Indítsd el a szervert (leállítás: ctrl+c)

        jboss-as-7.1.3.Final/bin/standalone.sh

* Regisztráld a drivert: jboss-cli segítségével:

        /subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)

* Indítsd újra a szervert és ellenőrizd, hogy látsz-e valami hasonló sort:

        INFO [org.jboss.as.connector.subsystems.datasources] JBAS010404: Deploying non-JDBC-compliant driver class org.postgresql.Driver

#### Alkalmazás konfigurálása

* Datasource felvétele: jboss-cli

        xa-data-source add --name=schkp --driver-name=postgresql --jndi-name=java:/jdbc/sch --user-name=kir --password=alma123 --use-ccm=false --max-pool-size=25 --min-pool-size=10 --pool-prefill=true --prepared-statements-cache-size=30

        /subsystem=datasources/xa-data-source=schkp/xa-datasource-properties=ServerName:add(value=localhost)
        /subsystem=datasources/xa-data-source=schkp/xa-datasource-properties=PortNumber:add(value=5432)
        /subsystem=datasources/xa-data-source=schkp/xa-datasource-properties=DatabaseName:add(value=vir)
        xa-data-source enable --name=schkp

* mail resource hozzáadása

        /subsystem=mail/mail-session="java:/mail/korokMail":add(from=kir-dev`sch.bme.hu,jndi-name=java:/mail/korokMail)

* Java property-k. `appdata/korok/` könyvtár tartalma: `config.properties, logback.xml` (`resources/` mappából kimásolhatók)

        /system-property=application.resource.dir:add(value=/home/balo/kir-dev/appdata)
        /system-property=logback.configurationFile:add(value=/home/balo/kir-dev/appdata/korok/logback.xml)

    Az elérési utak értelemszerűen modosítandóak.

* Kapcsold ki a welcome-root-ot: jboss-cli

        /subsystem=web/virtual-server=default-host:write-attribute(name=enable-welcome-root,value=false)

* Az `${application.resource.dir}/korok` mappában a `config.properties` fájlban állítsd be rád vonatozó értékeket..

### Build & Deploy

A projekt főkönyvtárából:

    mvn -DskipTests clean package jboss-as:deploy

## Front-end

TBA

## Windows alatt

Ha **Windows** alá telepíted **ne felejtsd el** a NetBeans-ben/Eclipse-
ben/akármilyen szövegszerkesztőben beállítani az egész projektre, hogy a forrás
**UTF-8**, különben nagyon hamar elszúrhatod az egész alkalmazást/repository-t!!!

[1]: http://www.postgresql.org/download/
[2]: http://jdbc.postgresql.org/download.html
[3]: https://docs.jboss.org/author/display/AS71/CLI+Recipes