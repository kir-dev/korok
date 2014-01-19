class Semester
  attr_reader :start_year, :end_year



  def initialize(start_year, end_year, autumn)
    @start_year = start_year
    @end_year = end_year
    @autumn = autumn
  end

  def autumn?
    @autumn
  end

  def to_s
    "#{@start_year}#{@end_year}#{autumn? ? 1 : 2}"
  end

  def previous
    if @autumn
      Semester.new @start_year-1, @end_year-1, false
    else
      Semester.new @start_year, @end_year, true
    end
  end

  def eql?(other)
    return false unless other.kind_of? Semester

    return @start_year == other.start_year && @end_year == other.end_year && @autumn == other.autumn?
  end

  alias_method :==, :eql?

  def self.generate(from, to)
    semesters = []
    (from..(to-1)).each do |year|
      semesters << Semester.new(year, year+1, true)
      semesters << Semester.new(year, year+1, false)
    end

    semesters
  end
end
