package hu.sch.ejb.test.builder;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.Valuation;
import hu.sch.domain.enums.ValuationStatus;
import hu.sch.domain.user.User;
import java.util.Date;

/**
 *
 * @author ksisu
 */
public class ValuationBuilder extends AbstractBuilder<Valuation> {

    private Group group = null;
    private User sender = null;
    private String valuationText = "";
    private String principle = "";
    private ValuationStatus pointStatus = ValuationStatus.ELBIRALATLAN;
    private ValuationStatus entrantStatus = ValuationStatus.ELBIRALATLAN;
    private Semester semester = new Semester("200020011");
    private Date sended = new Date();
    private Date lastModified = new Date();
  
    public ValuationBuilder withGroup(Group group) {
        this.group = group;
        return this;
    }

    public ValuationBuilder withSender(User sender) {
        this.sender = sender;
        return this;
    }

    public ValuationBuilder withValuationText(String valuationText) {
        this.valuationText = valuationText;
        return this;
    }

    public ValuationBuilder withPrinciple(String principle) {
        this.principle = principle;
        return this;
    }

    public ValuationBuilder withPointStatus(ValuationStatus pointStatus) {
        this.pointStatus = pointStatus;
        return this;
    }

    public ValuationBuilder withEntrantStatus(ValuationStatus entrantStatus) {
        this.entrantStatus = entrantStatus;
        return this;
    }

    public ValuationBuilder withSemester(Semester semester) {
        this.semester = semester;
        return this;
    }

    public ValuationBuilder withSended(Date sended) {
        this.sended = sended;
        return this;
    }

    public ValuationBuilder withLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Override
    public Valuation build() {
        Valuation v = new Valuation();
        v.setGroup(group);
        v.setSender(sender);
        v.setValuationText(valuationText);
        v.setPrinciple(principle);
        v.setPointStatus(pointStatus);
        v.setEntrantStatus(entrantStatus);
        v.setSemester(semester);
        v.setSended(sended);
        v.setLastModified(lastModified);
        v.setConsidered(false);
        return v;
    }
}
