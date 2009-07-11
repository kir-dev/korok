/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import org.apache.log4j.Logger;
import org.apache.wicket.IResponseFilter;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 *
 * @author aldaris
 */
public class ServerTimerFilter implements IResponseFilter {

    private static final Logger log = Logger.getLogger(ServerTimerFilter.class);

    public AppendingStringBuffer filter(AppendingStringBuffer responseBuffer) {
        long timeTaken = System.currentTimeMillis() - RequestCycle.get().getStartTime();
        StringBuilder sb = new StringBuilder(100);
        sb.append(RequestCycle.get().getRequest().getPath());
        sb.append(" oldalhoz szükséges kiszolgálási idő: ");
        sb.append(timeTaken);
        sb.append("ms");
        log.info(sb.toString());
        return responseBuffer;
    }
}
