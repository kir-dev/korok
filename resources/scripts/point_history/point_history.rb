require_relative "../pek-common"
require_relative "semester.rb"

options = OptParser.parse(ARGV)
puts # needs this to have an extra CRLF after password input

DB = Database.init options

SQL = <<EOL
SELECT p.user_id AS usr_id, LEAST(TRUNC(SQRT(SUM(p.sum * p.sum))),100) AS point
FROM
(
    SELECT pontigenyles.usr_id AS user_id, SUM(pontigenyles.pont) AS sum
    FROM ertekelesek v
    RIGHT JOIN pontigenyles ON pontigenyles.ertekeles_id = v.id
    WHERE
      v.next_version IS NULL
      AND v.pontigeny_statusz = 'ELFOGADVA'
      AND (v.semester = ? OR v.semester = ?)
    GROUP BY v.grp_id, pontigenyles.usr_id
) AS p
GROUP BY p.user_id
EOL

# first semester is 199920002 and last is 201320141, so we skip the first
# and last ones
semesters = Semester.generate(1999, 2014)[1..-2]

file = File.new "point_history.sql", "w"

semesters.each do |sem|
  file.puts "-- begining of #{sem} semester"
  DB.fetch(SQL, sem.previous.to_s, sem.to_s) do |row|
    file.puts "INSERT INTO point_history(usr_id, point, semester) VALUES (#{row[:usr_id]}, #{row[:point].to_i}, '#{sem}');"
  end
  file.puts "-- end of #{sem} semester"
end

file.close
