--
-- Copyright (c) 2009-2010, Peter Major
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions are met:
-- * Redistributions of source code must retain the above copyright
-- notice, this list of conditions and the following disclaimer.
--  * Redistributions in binary form must reproduce the above copyright
-- notice, this list of conditions and the following disclaimer in the
-- documentation and/or other materials provided with the distribution.
--  * Neither the name of the Peter Major nor the
-- names of its contributors may be used to endorse or promote products
-- derived from this software without specific prior written permission.
--  * All advertising materials mentioning features or use of this software
-- must display the following acknowledgement:
-- This product includes software developed by the Kir-Dev Team, Hungary
-- and its contributors.
--
-- THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
-- EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
-- WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
-- DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
-- (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
-- LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
-- ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
-- (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
-- SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--

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