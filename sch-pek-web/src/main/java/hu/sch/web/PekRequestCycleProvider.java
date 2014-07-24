package hu.sch.web;

import hu.sch.services.config.Configuration;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;

public class PekRequestCycleProvider implements IRequestCycleProvider {

    private final Configuration config;

    public PekRequestCycleProvider(Configuration config) {
        this.config = config;
    }

    @Override
    public RequestCycle get(RequestCycleContext context) {
        if (config.getEnvironment() == Configuration.Environment.PRODUCTION) {
            context.setExceptionMapper(new DefaultExceptionMapper());
        }
        return new RequestCycle(context);
    }

}
