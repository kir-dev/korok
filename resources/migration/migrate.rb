# encoding: utf-8
#
# Címtár -> DB migriló szkript.
# https://gist.github.com/tmichel/5939782
#
# Használat:
# ruby migrate.rb /path/to/uploaded/photoes

# check for provided options
exit if ARGV.size < 1

require "rubygems"
require "bundler/setup"
require "yaml"
require "sequel"
require 'net-ldap'
require "logger"

require_relative "mappings.rb"

# constants
BASE_DN = 'ou=people,ou=sch,o=bme,c=hu'
VIRID_URN = 'urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:'
EMPTY = ""

# globals
$cntr = 0
LOGGER = Logger.new("migration.log")

# helper methods

# fills out required but empty attributes
def fill_mandatory_attributes(record, new_record)
  record[:usr_id] = $DB["SELECT nextval('users_usr_id_seq') as id"].first[:id] if new_record
  record[:usr_screen_name] ||= "userX#{$cntr+=1}"

  if new_record
    [:usr_firstname, :usr_lastname].each do |col|
      record[col] = "tmp_placeholder"
      LOGGER.warn "No #{col} for existing record: #{record[:usr_id]}"
    end
  end
end

def strip_neptun(raw_neptun)
  raw = Array(raw_neptun).first
  if raw
    last_sep = raw.rindex ':'
    raw[last_sep+1,6].upcase
  else
    nil
  end
end

# end of helper methods

config_file = File.join File.dirname(__FILE__), "config.yml"
config = YAML.load_file config_file

# setup database connection
$DB = Sequel.postgres config["database"]

begin
  # checking $DB
  $DB[:users].first
rescue Exception => e
  puts e.message
  exit
end

# setup ldap connection
ldap_config = config["ldap"]
DS = Net::LDAP.new
DS.host = ldap_config["host"]
DS.port = ldap_config["port"]
DS.auth ldap_config["user"], ldap_config["password"]

unless DS.bind
  p 'Datastore bind failed'
  p DS.get_operation_result
  exit
end

mapping = {
  "uid" => Mappings::Base.new(:usr_screen_name),
  "sn" => Mappings::Base.new(:usr_lastname, true),
  "givenName" => Mappings::Base.new(:usr_firstname, true),
  "displayName" => Mappings::Base.new(:usr_nickname, true),
  "schacDateOfBirth" => Mappings::Date.new(:usr_date_of_birth),
  "roomNumber" => Mappings::RoomNumber.new,
  "mail" => Mappings::Base.new(:usr_email),
  "schacUserStatus" => Mappings::StudentStatus.new,
  "mobile" => Mappings::Base.new(:usr_cell_phone),
  "homePostalAddress" => Mappings::Base.new(:usr_home_address, true),
  "labeledURI" => Mappings::Base.new(:usr_webpage),
  "schacGender" => Mappings::Gender.new,
  "sch-vir-mothersName" => Mappings::Base.new(:usr_mother_name, true),
  "sch-vir-estimatedGraduationYear" => Mappings::Base.new(:usr_est_grad),
  "jpegPhoto" => Mappings::Photo.new,
  "schacPersonalUniqueCode" => Mappings::Neptun.new,
  "inetUserStatus" => Mappings::UserStatus.new,
  "schacUserPresenceID" => Mappings::IM.new,
  "schacUserPrivateAttribute" => Mappings::PrivateAttribute.new,
  "userPassword" => Mappings::Password.new
}

processed_vir_ids = []

# iterate over all of the entries
DS.search(:base => BASE_DN, :return_result => false) do |entry|
  next if entry.dn == "ou=people,ou=sch,o=bme,c=hu"

  vir_id_raw = entry["schacPersonalUniqueID"].first
  virid = vir_id_raw ? vir_id_raw.sub(VIRID_URN, EMPTY) : nil

  new_record = false

  if virid.nil?
    # try finding by neptun just in case
    record = $DB[:users].first 'upper(usr_neptun) = ?', strip_neptun(entry['schacPersonalUniqueCode'])

    # still cant find record: create new
    unless record
      new_record = true
      LOGGER.info "No VIRID for DN: #{entry.dn}. Creating new record in database."
    end
  end

  # get the record
  if new_record
    record = {}
  elsif record.nil? # if we already have a record (queried by neptun)
    record = $DB[:users].first(:usr_id => virid)
  end

  if record.nil?
    LOGGER.error "!!!! Record should exist for id: #{virid}, dn: #{entry.dn}"
    next
  end

  fill_mandatory_attributes record, new_record

  if new_record
    virid = $DB[:users].insert record
    record = $DB[:users].first :usr_id => virid
  end

  # apply mappings
  mapping.each do |k,v|
    next if entry[k].empty?

    v.copy entry[k], record
  end

  begin
    # save the record to DB
    $DB[:users].where(:usr_id => virid).update(record)
  rescue Exception => e
    LOGGER.error e.message
    LOGGER.error "error with record: #{record}\nentry: #{entry}"
  end
  processed_vir_ids << record[:usr_id]
end

puts "Processed #{processed_vir_ids.size} records."
