<?php

$VIR = new PDO("pgsql:host=localhost port=5433 dbname=vir user=kir password=****");
$NEW = new PDO("pgsql:host=localhost port=5432 dbname=vir user=kir password=****");
$VIR->query("SET NAMES 'UTF-8'");
$NEW->query("SET NAMES 'UTF-8'");
$VIR->query("SET CLIENT_ENCODING 'UTF-8'");
$NEW->query("SET CLIENT_ENCODING 'UTF-8'");

echo "Adatbázis ok\n";


$stmt = $VIR->query("SELECT grp_id, semester, req_report FROM request_stats ORDER BY semester, grp_id;");
foreach ($stmt as $ertekeles) {
    echo "Új értékelés létrehozása grp_id=".$ertekeles['grp_id']." and semester=".$ertekeles['semester']. "\n";
    $ertekeles_id = create_ertekeles($ertekeles['grp_id'],$ertekeles['semester'],$ertekeles['req_report']);

}


function execute($conn, $statement) {
    if (! $statement->execute()) {
        $conn->rollBack();
        print_r($conn->errorInfo());
        die("Error");
    }
}


function create_belepoigenyles($grp_id, $semester, $ertekeles_id) {
    global $VIR,$NEW;

    $stmt = $VIR->prepare("SELECT usr_id, card_type, crd_req_cause FROM card_request WHERE grp_id=:grp AND semester=:sem");
    $stmt->bindValue('grp', $grp_id, PDO::PARAM_INT);
    $stmt->bindValue('sem', $semester);

    execute($VIR, $stmt);

    foreach ($stmt->fetchAll() as $belepo) {
        if ($belepo['card_type'] == 'ÁB') {
            $belepo['card_type'] = 'AB';
        }
        echo "Belépõigénylés betöltése usr_id=".$belepo['usr_id']." belepo=".$belepo['card_type']."\n";

        $stmt = $NEW->prepare("INSERT INTO belepoigenyles (id, usr_id, belepo_tipus, ertekeles_id) VALUES ((SELECT nextval('hibernate_sequence')), :usr, :pt, :ert)");
        $stmt->bindValue('usr', $belepo['usr_id'],PDO::PARAM_INT);
        $stmt->bindValue('pt', $belepo['point_req_point'],PDO::PARAM_INT);
        $stmt->bindValue('ert', $ertekeles_id, PDO::PARAM_INT);

        execute($NEW, $stmt);
    }
}

function create_pontigenyles($grp_id, $semester, $ertekeles_id) {
    global $VIR,$NEW;

    $stmt = $VIR->prepare("SELECT usr_id, point_req_point FROM point_request WHERE grp_id=:grp AND semester=:sem");
    $stmt->bindValue('grp', $grp_id, PDO::PARAM_INT);
    $stmt->bindValue('sem', $semester);

    execute($VIR, $stmt);

    foreach ($stmt->fetchAll() as $pont) {
        echo "Pontigénylés betöltése usr_id=".$pont['usr_id']." pont=".$pont['point_req_point']."\n";

        $stmt = $NEW->prepare("INSERT INTO pontigenyles (id, usr_id, pont, ertekeles_id) VALUES ((SELECT nextval('hibernate_sequence')), :usr, :pt, :ert)");
        $stmt->bindValue('usr', $pont['usr_id'],PDO::PARAM_INT);
        $stmt->bindValue('pt', $pont['point_req_point'],PDO::PARAM_INT);
        $stmt->bindValue('ert', $ertekeles_id, PDO::PARAM_INT);

        execute($NEW, $stmt);
    }
}

function create_uzenetek($grp_id, $semester, $ertekeles_id) {
    global $VIR,$NEW;

    $stmt = $VIR->prepare("SELECT usr_id, req_comment_text, req_comment_time FROM request_comment WHERE grp_id=:grp AND semester=:sem");
    $stmt->bindValue('grp', $grp_id, PDO::PARAM_INT);
    $stmt->bindValue('sem', $semester);

    execute($VIR, $stmt);

    foreach ($stmt->fetchAll() as $uzenet) {
        echo "Üzenet betöltése usr_id=".$uzenet['usr_id']."\n";

        $stmt = $NEW->prepare("INSERT INTO ertekeles_uzenet (id, feladas_ido, uzenet, felado_usr_id, ertekeles_id) VALUES ((SELECT nextval('hibernate_sequence')), :ido, :uz, :usr, :ert)");
        $stmt->bindValue('ido', $uzenet['req_comment_time']);
        $stmt->bindValue('uz', $uzenet['req_comment_text']);
        $stmt->bindValue('usr', $uzenet['usr_id'],PDO::PARAM_INT);
        $stmt->bindValue('ert', $ertekeles_id, PDO::PARAM_INT);

        execute($NEW, $stmt);
    }
}


function create_ertekeles($grp_id, $semester, $text) {
    global $NEW,$VIR;

    $NEW->beginTransaction();
    $res = $NEW->query("SELECT nextval('hibernate_sequence');");
    foreach ($res as $v) {
        $ertekeles_id = array_pop($v);
    }
    if (!$ertekeles_id) {
        die('error, nincs sequence...');
    }
    echo "inserting ertekeles with id=$ertekeles_id \n";
    if (empty($text)) {
        $text = "Nincs megadott szöveges értékelés";
    }
    $stmt = $NEW->prepare("INSERT INTO ertekelesek (id, semester, pontigeny_statusz, belepoigeny_statusz, szoveges_ertekeles, grp_id) VALUES (:ertekeles_id, :sem, 'ELFOGADVA', 'ELFOGADVA', :szov, :grp)");
    $stmt->bindValue(":ertekeles_id", $ertekeles_id, PDO::PARAM_INT);
    $stmt->bindValue(":sem", $semester);
    $stmt->bindValue(":szov", $text);
    $stmt->bindValue(":grp", $grp_id, PDO::PARAM_INT);

    execute($NEW, $stmt);
    create_uzenetek($grp_id, $semester, $ertekeles_id);
    create_pontigenyles($grp_id, $semester, $ertekeles_id);
    create_belepoigenyles($grp_id, $semester, $ertekeles_id);


    $NEW->commit();
}
