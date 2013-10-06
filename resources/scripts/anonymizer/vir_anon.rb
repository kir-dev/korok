# encoding: utf-8
#/usr/bin/ruby -w

require 'rubygems'
require 'net-ldap'
require 'sequel'

# DON'T COMMMIT THE **** PASSWORDS FOR HEAVEN'S SAKE
DB_URL = 'postgres://kir:passwd@localhost/vir'
LDAP_HOST = 'localhost'
LDAP_PORT = 5389
LDAP_USER = 'cn=Directory Manager'
LDAP_PW = ''

BASE_DN = 'ou=people,ou=sch,o=bme,c=hu'
VIRID_URN = 'urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:'
NEPTUN_URN = 'urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:'

# http://net-ldap.rubyforge.org/Net/LDAP.html

def ds_init
  ldap = Net::LDAP.new
  ldap.host = LDAP_HOST
  ldap.port = LDAP_PORT
  ldap.auth LDAP_USER, LDAP_PW

  unless ldap.bind
    p 'Datastore bind failed'
    p ldap.get_operation_result
    exit
  end

  ldap
end

def get_anon_mail(counter)
  "pek.juzer#{counter}@devnull.stewie.sch.bme.hu"
end

def get_anon_neptun(counter)
  'A'+("%05d" % [counter])
end

def get_anon_firstname(counter)
  "Júzer #{counter}"
end

def get_anon_lastname
  "Pék"
end

def get_anon_displayname(counter)
  "pék #{counter}"
end

def get_anon_roomnumber(counter)
  "Schönherz #{counter}"
end

def get_anon_homepostaladdress(counter)
  "4321 Bugyi, Köztársaság u. 11., PF #{counter}"
end

def modify_ds_entry(ds_conn, entry, counter)
  delete_attrs = [:jpegphoto, :mobile, :homenumber,
    :labeleduri, :schacuserpresenceid,
    :"sun-fm-saml2-nameid-infokey", :"sun-fm-saml2-nameid-info",
    :sunamauthinvalidattemptsdata ]

  delete_attrs.each { |e|
    unless ds_conn.delete_attribute entry.dn, e
      unless 16 == ds_conn.get_operation_result.code # No Such Attribute, don't care
        p "WARNING: Tried to delete attr: #{e} << #{ds_conn.get_operation_result.message} (#{ds_conn.get_operation_result.code})"
      end
   end
  }

  replace_attrs = {:cn => get_anon_lastname + ' ' + get_anon_firstname(counter),
                   :sn => get_anon_lastname,
                   :givenname => get_anon_firstname(counter),
                   :mail => get_anon_mail(counter),
                   :userpassword => "x"
                  }

  # replace (=readd!) attributes only if they exist
  if entry.attribute_names.include?(:displayname)
    replace_attrs[:displayname] = get_anon_displayname(counter)
  end

  if entry.attribute_names.include?(:roomnumber)
    replace_attrs[:roomnumber] = get_anon_roomnumber(counter)
  end

  if entry.attribute_names.include?(:schacdateofbirth)
    replace_attrs[:schacdateofbirth] = '19911225'
  end

  if entry.attribute_names.include?(:"sch-vir-mothersname")
    replace_attrs[:"sch-vir-mothersname"] = "Pékné Alma#{counter}"
  end

  if entry.attribute_names.include?(:homepostaladdress)
    replace_attrs[:homepostaladdress] = get_anon_homepostaladdress(counter)
  end

  if entry.attribute_names.include?(:schacpersonaluniquecode)  # neptun
    replace_attrs[:schacpersonaluniquecode] = NEPTUN_URN + get_anon_neptun(counter)
  end

  replace_attrs.each_pair { |name, val|
    unless ds_conn.replace_attribute entry.dn, name, val
      p "WARNING: Tried to replace attr: #{e} << #{ds_conn.get_operation_result.message} (#{ds_conn.get_operation_result.code})"
    end
  }
end

def modify_db_entry(db_conn, counter, db_user)
  mods = {}

  if db_user[:usr_email] != nil and !db_user[:usr_email].empty?
    mods[:usr_email] = get_anon_mail(counter)
  end

  if db_user[:usr_neptun] != nil and !db_user[:usr_neptun].empty?
    mods[:usr_neptun] = get_anon_neptun(counter)
  end

  if db_user[:usr_firstname] != nil and !db_user[:usr_firstname].empty?
    mods[:usr_firstname] = get_anon_firstname(counter)
  end

  if db_user[:usr_lastname] != nil and !db_user[:usr_lastname].empty?
    mods[:usr_lastname] = get_anon_lastname
  end

  if db_user[:usr_nickname] != nil and !db_user[:usr_nickname].empty?
    mods[:usr_nickname] = get_anon_displayname(counter)
  end

  if mods.length > 0
    db_conn.from(:users)
            .where('usr_id = ?', db_user[:usr_id])
            .update(mods)
  end
end

def modify_db_only_users(db_conn, counter, virids)
  #anonimize users who are only in the virdb
  db_conn.from(:users)
        .where("usr_id NOT IN (#{virids.join(',')})")
        .each { |user|

    counter += 1

    p "usr_id=#{user[:usr_id]}"

    modify_db_entry(db_conn, counter, user)
  }
end

def anonimize(ds_conn, db_conn)
  counter = 0;
  virids = []

  p 'truncate some tables...'
  db_conn.run("truncate table neptun_list cascade")
  db_conn.run("truncate table user_attrs")
  db_conn.run("update users set usr_passwd = ''")

  ds_conn.open do |ldap|
    p 'search entries'
    entries = ldap.search(:base => BASE_DN, #:attributes => ATTRS,
                   :filter => Net::LDAP::Filter.eq("objectclass", "person"),
                   :return_result => true)

    p 'modify entries'
    # iterate through the returned resultset
    # because modify in the search don't seem to work (connection terminated)
    entries.each { |entry|
      counter += 1

      p entry.dn
      modify_ds_entry(ldap, entry, counter)

      # search and update entry.virid in db
      if entry.attribute_names.include?(:schacpersonaluniqueid)
        virid = entry.schacpersonaluniqueid.first.gsub(VIRID_URN, '')

        db_user = db_conn.from(:users).where('usr_id = ?', virid).first
        modify_db_entry(db_conn, counter, db_user)

        virids.push(virid)
      end
    }
  end

  p 'modify users who only exist in the db...'
  modify_db_only_users(db_conn, counter, virids)
end

time_begin = Time.now
p "begin"

db_conn = Sequel.connect(DB_URL)
ds_conn = ds_init
anonimize(ds_conn, db_conn)

p "end... elapsed time=" + (Time.now - time_begin).to_s
