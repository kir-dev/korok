package hu.sch.web.wicket.util;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.response.filter.IResponseFilter;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public class ServerTimerFilter implements IResponseFilter {

    private static final Logger log = LoggerFactory.getLogger(ServerTimerFilter.class);

    @Override
    public AppendingStringBuffer filter(AppendingStringBuffer responseBuffer) {
        long timeTaken = System.currentTimeMillis() - RequestCycle.get().getStartTime();
        StringBuilder sb = new StringBuilder(100);
        sb.append(RequestCycle.get().getRequest().getFilterPath());
        sb.append(" oldalhoz szükséges kiszolgálási idő: ");
        sb.append(timeTaken);
        sb.append("ms");
        log.info(sb.toString());
        return responseBuffer;
    }
}
