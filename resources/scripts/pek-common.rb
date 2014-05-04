require "sequel"
require "optparse"
require "io/console"
require "ostruct"

module OptParser
  def self.parse(args)
    options = OpenStruct.new

    options.host = "localhost"
    options.port = 5432
    options.user = "kir"
    options.database = "vir"
    options.password = ""

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

    if block_given?
      unless yield(options)
        puts 'Not enough arguments!\n'
        puts opt_parser.to_a
        exit
      end
    end

    # gets the password
    print "Password: "
    options.password = STDIN.noecho(&:gets).chomp

    options
  end
end

module Database
  def self.init(options)
    db = Sequel.postgres :host => options.host,
                         :user => options.user,
                         :password => options.password,
                         :database => options.database,
                         :port => options.port

    begin
      # checking for db password correctness
      db[:users].first
    rescue Exception => e
      puts e.message
      exit
    end

    db
  end
end
