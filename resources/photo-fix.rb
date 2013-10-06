# Fix for photo paths. Migration script screwed it a up a bit. :)
#
# Usage:
#   ruby photo-fix.rb [options] photo_path
require "sequel"
require "optparse"
require "io/console"
require "ostruct"

module OptParser
  def self.parse(args)
    options = OpenStruct.new

    options.host = "localhost"
    options.port = 5423
    options.user = "kir"
    options.database = "vir"
    options.password = ""
    options.photo_path = ""

    opt_parser = OptionParser.new do |opts|
      opts.banner = "Usage photo-fix.rb [OPTIONS] photo_path"

      opts.on "-u", "--user [USER]", "Database user. Defaults to kir" do |user|
        options.user = user
      end

      opts.on "-d", "--database [DATABASE]", "Database. Defaults to vir" do |db|
        options.database = db
      end

      opts.on "-h", "--host [HOST]", "Database host. Defaults to localhost" do |host|
        options.host = host
      end

      opts.on "-P", "--port [PORT]", Integer, "Database port. Defaults to 5432" do |port|
        options.password = port
      end

      opts.on_tail("-?", "--help", "Show this message") do
        puts opts
        exit
      end
    end

    opt_parser.parse!(args)

    # OptionParser removes persed arguments
    if ARGV.size != 1
      puts "Not enough arguments!"
      puts
      puts opt_parser.to_a
      exit
    else
      options.photo_path = ARGV[0]
    end

    # gets the password
    print "Password: "
    options.password = STDIN.noecho(&:gets).chomp

    options
  end
end

options = OptParser.parse(ARGV)
puts # needs this to have an extra CRLF after password input

DB = Sequel.postgres :host => options.host,
                     :user => options.user,
                     :password => options.password,
                     :database => options.database,
                     :port => options.port

begin
  # checking for db password correctness
  DB[:users].first
rescue Exception => e
  puts e.message
  exit
end

users = DB[:users]
photoes = DB["SELECT usr_id, usr_photo_path FROM users WHERE usr_photo_path IS NOT NULL"]

re = /^\((\w),(\w+),(\w+\.png)\)/i

updates = []

photoes.each do |user|
  re.match(user[:usr_photo_path]) do |m|
    new_path = "#{m[1]}/#{m[2]}/#{m[3]}".downcase
    updates << users.where(:usr_id => user[:usr_id]).update_sql(:usr_photo_path => new_path)
  end
end

puts "Writing sql updates to file..."

File.open "photo-fix-updates.sql", "w" do |f|
  f.puts "begin;"
  f.puts(updates.join(";\n") << ";")
  f.puts "commit;"
end

puts "Writing file system mv-s to file..."

f = File.new "photo-fix-moves.sh", "w"
Dir[File.join(File.expand_path(options.photo_path), "**", "*.png")].each do |path|
  if path != path.downcase
    f.puts "mkdir -p #{File.dirname(path).downcase}"
    f.puts "mv #{path} #{path.downcase}"
  end
end
f.close

puts "Done."
