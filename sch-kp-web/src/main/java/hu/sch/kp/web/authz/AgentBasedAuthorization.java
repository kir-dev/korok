package hu.sch.kp.web.authz;

import hu.sch.domain.Csoport;
import hu.sch.domain.TagsagTipus;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebRequest;

/**
 *
 * @author hege
 */
public class AgentBasedAuthorization implements UserAuthorization {

    private final static Logger log =
            Logger.getLogger(AgentBasedAuthorization.class);
    private static final String ENTITLEMENT_CACHE =
            "hu.sch.kp.web.authz.EntitlementCache";
    private final static Pattern VIRID_PATTERN =
            Pattern.compile("^.*:([0-9]+)$");
    private final static Pattern ENTITLEMENT_PATTERN =
            //                          jog:csoportnév:csoportid
            Pattern.compile("^.*:entitlement:([^:]+):([^:]+):([0-9]+)$");
    private static final String VIRID_ATTRNAME = "virid";
    private static final String ENTITLEMENT_ATTRNAME = "eduPersonEntitlement";

    public void init(Application wicketApplication) {
    }

    public Long getUserid(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();
        Set viridSet = (Set) servletRequest.getAttribute(VIRID_ATTRNAME);

        if (viridSet != null) {
            for (Object virid : viridSet) {
                Matcher m = VIRID_PATTERN.matcher(virid.toString());
                if (m.matches()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Parsed userid from agent: " + m.group(1));
                    }
                    return Long.parseLong(m.group(1));
                }
            }
        }
        return null;
    }

    public boolean hasAbstractRole(Request wicketRequest, String role) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();
        boolean inRole = servletRequest.isUserInRole(role);
        if (log.isDebugEnabled()) {
            log.debug("Check container role: " + role + ": " +
                    (inRole ? "true" : "false"));
        }

        return inRole;
    }

    public boolean hasRoleInGroup(Request wicketRequest, Csoport csoport, TagsagTipus tagsagTipus) {
        Map<Long, TagsagTipus> tagsagok = parseEntitlements(wicketRequest);
        TagsagTipus ttip = tagsagok.get(csoport.getId());
        if (ttip == null) {
            return false;
        }

        return ttip.equals(tagsagTipus);
    }

    public boolean hasRoleInSomeGroup(Request wicketRequest, TagsagTipus tagsagTipus) {
        Map<Long, TagsagTipus> tagsagok = parseEntitlements(wicketRequest);

        return tagsagok.values().contains(tagsagTipus);
    }

    private Map<Long, TagsagTipus> parseEntitlements(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();

        Map<Long, TagsagTipus> tagsag =
                (Map<Long, TagsagTipus>) servletRequest.getAttribute(ENTITLEMENT_CACHE);
        if (tagsag != null) {
            log.debug("Reusing request entitlement cache");

            return tagsag;
        }

        tagsag = new HashMap<Long, TagsagTipus>();
        servletRequest.setAttribute(ENTITLEMENT_CACHE, tagsag);

        Set entitlementSet =
                (Set) servletRequest.getAttribute(ENTITLEMENT_ATTRNAME);

        if (entitlementSet != null) {
            for (Object entitlement : entitlementSet) {
                String[] entitlements = entitlement.toString().split("\\|");
                for (String string : entitlements) {
                    Matcher m =
                            ENTITLEMENT_PATTERN.matcher(string);
                    if (m.matches()) {
                        String tagsagTipus = m.group(1);
                        String csoportNev = m.group(2);
                        Long csoportId = Long.parseLong(m.group(3));

                        if (log.isDebugEnabled()) {
                            log.debug("Entitlement: csoportNev: " + csoportNev +
                                    " ,tagsagTipus: " + tagsagTipus +
                                    " , csoportId: " + csoportId.toString());
                        }
                        TagsagTipus tt =
                                TagsagTipus.fromEntitlement(tagsagTipus);
                        if (tt == null) {
                            log.warn("Cannot map entitlement " + tagsagTipus);
                            continue;
                        }
                        TagsagTipus temp = tagsag.get(csoportId);
                        if (temp != null) {
                            if (temp != TagsagTipus.KORVEZETO && tt == TagsagTipus.KORVEZETO) {
                                tagsag.put(csoportId, tt);
                            }
                        } else {
                            tagsag.put(csoportId, tt);
                        }
                    }
                }
            }
        }

        return tagsag;
    }

    /*
    private Set getTestEntitlementAttribute() {
    Set set = new HashSet();
    set.add("urn:mace:sch.hu:entitlement:korvezeto:KIR fejlesztők és üzemeltetők:106");
    set.add("urn:mace:sch.hu:entitlement:tag:SPOT:13");

    return set;
    }

    private Set getTestViridAttribute() {
    Set set = new HashSet();
    set.add("urn:terena:schac:schacUniqueId:16227");

    return set;
    }
     */
}
