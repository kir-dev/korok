package hu.sch.ejb.test.builder;

import hu.sch.domain.PointRequest;
import hu.sch.domain.Valuation;
import hu.sch.domain.user.User;

/**
 *
 * @author ksisu
 */
public class PointRequestBuilder extends AbstractBuilder<PointRequest> {

    private User user = null;
    private Valuation valuation = null;
    private Integer point = 0;

    public PointRequestBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public PointRequestBuilder withValuation(Valuation valuation) {
        this.valuation = valuation;
        return this;
    }

    public PointRequestBuilder withPoint(Integer point) {
        this.point = point;
        return this;
    }

    @Override
    public PointRequest build() {
        if(valuation == null){
            valuation = new ValuationBuilder().build();
        }
        PointRequest pr = new PointRequest();
        pr.setPoint(point);
        pr.setUser(user);
        pr.setValuation(valuation);
        return pr;
    }
}
