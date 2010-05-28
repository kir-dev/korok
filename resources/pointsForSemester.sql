CREATE TYPE user_points AS (neptun CHAR(6), points NUMERIC);

CREATE OR REPLACE FUNCTION getPointsForSemester(text, text) RETURNS SETOF user_points AS
$$
SELECT UPPER(users.usr_neptun) AS neptun,
LEAST(vegso.atlag,100) FROM
(SELECT p.user_id AS usr_id, TRUNC(SQRT(SUM(p.sum * p.sum))) AS atlag FROM
(SELECT pontigenyles.usr_id AS user_id, v.grp_id, SUM(pontigenyles.pont) AS sum FROM ertekelesek v
RIGHT JOIN pontigenyles ON pontigenyles.ertekeles_id = v.id
WHERE v.pontigeny_statusz = 'ELFOGADVA' AND (v.semester = $1 OR v.semester = $2 )
GROUP BY v.grp_id, pontigenyles.usr_id) AS p
GROUP BY p.user_id) AS vegso
INNER JOIN users ON users.usr_id = vegso.usr_id AND users.usr_neptun IS NOT NULL
ORDER BY neptun ASC
$$
LANGUAGE 'SQL';