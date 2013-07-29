# encoding: utf-8
#
# Import script a SPOT-tól kapott gólya képek importálására.
# A képeket a dest/recommended/ mappába másolja. Várhatóan <500
# fájlról van szó, így elméletileg performancia problémákba nem ütközünk.
#
# Az elvárt fájlnév: csak a hallgató neptunkódját és a kiterjesztést tartalmazza.
#
# A `spot_images` tábla tartalmát törli az adatbázisból. Valamint a (ha létezik)
# a dest/recommended mappa tartalmát is törli.
#
# Használat:
#   ruby spot-import.rb [options] src dest

require "rubygems"
require "bundler/setup"
require "optparse"
require "ostruct"
require "sequel"
require "RMagick"
require "io/console"
require "digest"

require_relative "version.rb"

RECOMMENDED_FOLDER = "recommended"
IMAGE_MAX_SIZE = 400

module SpotImport
  class SpotOptParser
    def self.parse(args)
      options = OpenStruct.new

      options.host = "localhost"
      options.port = 5423
      options.user = "kir"
      options.database = "vir"
      options.password = ""
      options.dest = ""
      options.src = ""
      options.force = false

      opt_parser = OptionParser.new do |opts|
        opts.banner = "Usage spot-import.rb [OPTIONS] src dest"

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

        opts.on("-f", "--[no-]force", "Force run") do |f|
          options.force = f
        end

        opts.on_tail("-?", "--help", "Show this message") do
          puts opts
          exit
        end

        opts.on_tail("--version", "Show version") do
          puts "spot-import version: #{VERSION}"
          exit
        end
      end

      opt_parser.parse!(args)

      # OptionParser removes persed arguments
      if ARGV.size != 2
        puts "Not enough arguments!"
        puts
        puts opt_parser.to_a
        exit
      else
        options.src = ARGV[0]
        options.dest = File.join ARGV[1], RECOMMENDED_FOLDER
      end

      # gets the password
      print "Password: "
      options.password = STDIN.noecho(&:gets).chomp

      options
    end
  end
end

options = SpotImport::SpotOptParser.parse(ARGV)
source_images = Dir.glob(File.join(options.src, "*"))
total_number = source_images.size

puts # needs this to have an extra CRLF after password input

DB = Sequel.postgres :host => options.host,
                     :user => options.user,
                     :password => options.password,
                     :database => options.database

begin
  # checking for db password correctness
  DB[:spot_images].first
rescue Exception => e
  puts e.message
  exit
end

pattern = Regexp.new "^[0-9a-z]{6}$", Regexp::IGNORECASE

source_images.reject! do |f|
  file_name = File.basename f, ".*"
  if !pattern.match(file_name)
    warn "[WARNING]: #{f} filename is not a NEPTUN code. Skipping it!"
    true
  else
    false
  end
end

processed = source_images.size

if processed == 0
  warn "[WARNING]: No file matched."
  if !options.force
    puts "Exiting. To force running of the import process re-run with -f option."
    exit
  end
end

puts "Processing files..."
if !Dir.exists?(options.dest)
  FileUtils.mkdir_p options.dest
end

puts "\tDeleting files from destination directory..."
FileUtils.rm Dir.glob(File.join(options.dest, "*"))

images = {}

puts "\tCopying and resizing files..."
source_images.each do |f|
  img = Magick::Image.read(f).first
  img.resize_to_fit! IMAGE_MAX_SIZE, IMAGE_MAX_SIZE
  hash_file_name = "#{Digest::SHA1.hexdigest img.to_blob}#{File.extname(f)}"
  images[f] = hash_file_name
  img.write File.join(options.dest, hash_file_name)
end

puts "Processing database entries..."
spot_images = DB[:spot_images]

# delete images from last year
puts "\tDeleting existing records from..."
spot_images.delete

puts "\tInserting entries to database..."
source_images.each do |f|
  neptun = File.basename f, ".*"
  path = File.join RECOMMENDED_FOLDER, images[f]

  # insert
  spot_images.insert :usr_neptun => neptun, :image_path => path
end

puts "Done. Total number of files: #{total_number}. Processed: #{processed}."
