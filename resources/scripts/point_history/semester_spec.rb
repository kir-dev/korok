require_relative 'semester'

describe Semester do
  describe '#to_s' do
    it "prints 1 for autumn semester" do
      s = Semester.new 2013, 2014, true
      expect(s.to_s).to eq("201320141")
    end

    it "prints 2 for spring semester" do
      s = Semester.new 2013, 2014, false
      expect(s.to_s).to eq("201320142")
    end
  end

  describe "#eql?" do
    let(:semester) { Semester.new 2013, 2014, true }
    it "return false for not Semester objects" do
      expect(semester.eql?([])).to be_false
    end

    it "returns true only for exact match" do
      s1 = Semester.new 2013, 2014, true
      s2 = Semester.new 2012, 2013, true

      expect(semester.eql?(s1)).to be_true
      expect(semester.eql?(s2)).to be_false
    end
  end


  describe "#previous" do
    it "returns autumn semester for spring semester" do
      s = Semester.new 2013, 2014, false
      expect(s.previous).to eq(Semester.new(2013, 2014, true))
    end

    it "returns previous year's spring semester for autumn" do
      s = Semester.new 2013, 2014, true
      expect(s.previous).to eq(Semester.new(2012, 2013, false))
    end
  end

  describe "#generate" do
    it "yields every semester between given years" do
      sems = Semester.generate 2012, 2014

      expect(sems).not_to be_empty
      expect(sems.size).to eq(4)
    end
  end
end
