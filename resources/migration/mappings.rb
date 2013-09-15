require "date"
require "digest"
require "fileutils"
require "base64"

module Mappings

  class Base
    attr_accessor :attr, :force_utf8

    def initialize(attr = nil, force_utf8 = false)
      @attr = attr
      @force_utf8 = force_utf8
    end

    # copies the value of the attribute to the database
    def copy(attr_value, record)
      value = prepare_attr_value(attr_value)
      value.force_encoding 'utf-8' if force_utf8
      record[attr] = value
    end

  protected
    def prepare_attr_value(value)
      Array(value).first
    end
  end

  class Date < Base
    # parses the date and sets it for the given record
    def copy(attr_value, record)
      record[attr] = ::Date.parse prepare_attr_value(attr_value)
    rescue
      record[attr] = nil
    end
  end

  class StudentStatus < Base
    PREFIX = "urn:mace:terena.org:schac:status:sch.hu:student_status:"
    EMPTY = ""

    def copy(attr_value, record)
       value_in_ds = strip_value(prepare_attr_value(attr_value)).upcase
       if (value_in_ds == 'AKT') # fix: some people have invalid student_status
	 value_in_ds = 'ACTIVE'
       end
       record[:usr_student_status] = value_in_ds
    end

  private
    def strip_value(value)
      value.sub PREFIX, EMPTY
    end
  end

  class UserStatus < Base

    def copy(attr_value, record)
      record[:usr_status] = prepare_attr_value(attr_value).upcase
    end
  end

  class RoomNumber < Base

    def copy(attr_value, record)
      parts = prepare_attr_value(attr_value).split " "

      record[:usr_dormitory] = parts[0].force_encoding "utf-8"
      record[:usr_room] = parts[1]
    end
  end

  class Gender < Base
    MAPPING = {
      0 => "UNKNOWN",
      1 => "MALE",
      2 => "FEMALE",
      9 => "NOTSPECIFIED"
    }

    def copy(attr_value, record)
      record[:usr_gender] = MAPPING[prepare_attr_value(attr_value).to_i]
    end
  end

  class Neptun < Base

    EMPTY = ""

    def copy(attr_value, record)
      raw = prepare_attr_value(attr_value)
      last_sep = raw.rindex ':'
      neptun = raw[last_sep+1,6]

      record[:usr_neptun] = neptun.upcase
    end
  end

  class Photo < Base
    PATH = ARGV.first

    def copy(attr_value, record)
      blob = prepare_attr_value(attr_value)
      file_name = "#{Digest::SHA1.hexdigest blob}.png"
      path = record[:usr_screen_name][0], record[:usr_screen_name], file_name
      full_path = File.join PATH, path
      FileUtils.mkdir_p File.dirname(full_path)
      File.write full_path, blob
      record[:usr_photo_path] = path
    end
  end

  class IM < Base
    def copy(attr_value, record)
      Array(attr_value).each do |im|
        m = /(\w+):(.+)/.match im
        warn "Something went terribly wrong with IM parsing: #{im}" unless m

        protocol = m[1]
        account_name = m[2]
        if %w(jabber gtalk skype irc).include? protocol
          $DB[:im_accounts].insert :protocol => protocol, :account_name => account_name, :usr_id => record[:usr_id]
        end
      end
    end
  end

  class PrivateAttribute < Base
    MAPPING = {
      "uid" => "SCREEN_NAME",
      "schacGender" => "GENDER",
      "schacDateOfBirth" => "DATE_OF_BIRTH",
      "roomNumber" => "ROOM_NUMBER",
      "homePostalAddress" => "HOME_ADDRESS",
      "mail" => "EMAIL",
      "mobile" => "CELL_PHONE",
      "labeledURI" => "WEBPAGE",
    }
    def copy(attr_value, record)
      insert_data = {
        :usr_id => record[:usr_id],
      }
      values = Array(attr_value)

      MAPPING.each do |a,v|
        act_values = insert_data.merge :attr_name => v,
          :visible => !values.include?(a)
        $DB[:usr_private_attrs].insert act_values
      end
    end
  end

  class Password < Base
    SHA1_LENGTH = 20

    def copy(attr_value, record)
      value = prepare_attr_value attr_value
      unless value.start_with? "{SSHA}"
        ::LOGGER.error "Password hash format is broken, no SSHA at the begining. Value: #{value}, usr_id: #{record[:usr_id]}"
        return
      end

      raw_pwd = value.sub /\{SSHA\}/, ""
      decoded = Base64.decode64(raw_pwd).bytes.to_a

      salt_length = decoded.size - SHA1_LENGTH
      if salt_length <= 0
        ::LOGGER.error "Password scheme is invalid. No salt found."
        return
      end

      password_bytes = decoded[0, SHA1_LENGTH]
      salt_bytes = decoded[SHA1_LENGTH, salt_length]

      record[:usr_password] = Base64.strict_encode64 password_bytes.pack("c*")
      record[:usr_salt] = Base64.strict_encode64 salt_bytes.pack("c*")
    end
  end
end