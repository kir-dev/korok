package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.MembershipType;

import java.util.HashMap;
import java.util.Iterator;
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
    private final static Pattern NEPTUN_PATTERN =
            Pattern.compile("^.*:([A-Za-z0-9]{6,7})$");
    private final static Pattern ENTITLEMENT_PATTERN =
            //                          jog:csoportnév:csoportid
            Pattern.compile("^.*:entitlement:([^:]+):([^:]+):([0-9]+)$");
    private static final String VIRID_ATTRNAME = "virid";
    private static final String ENTITLEMENT_ATTRNAME = "eduPersonEntitlement";
    private static final String EMAIL_ATTRNAME = "mail";
    private static final String VEZETEKNEV_ATTRNAME = "sn";
    private static final String KERESZTNEV_ATTRNAME = "givenName";
    private static final String BECENEV_ATTRNAME = "displayName";
    private static final String NEPTUN_ATTRNAME = "neptun";

    public void init(Application wicketApplication) {
    }

    public Long getUserid(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();
        Set<?> viridSet = (Set<?>) servletRequest.getAttribute(VIRID_ATTRNAME);

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

    public boolean hasRoleInGroup(Request wicketRequest, Group csoport, MembershipType tagsagTipus) {
        Map<Long, MembershipType> tagsagok = parseEntitlements(wicketRequest);
        MembershipType ttip = tagsagok.get(csoport.getId());
        if (ttip == null) {
            return false;
        }

        return ttip.equals(tagsagTipus);
    }

    public boolean hasRoleInSomeGroup(Request wicketRequest, MembershipType tagsagTipus) {
        Map<Long, MembershipType> tagsagok = parseEntitlements(wicketRequest);

        return tagsagok.values().contains(tagsagTipus);
    }

    @SuppressWarnings("unchecked")
	private Map<Long, MembershipType> parseEntitlements(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();

        Map<Long, MembershipType> tagsag =
                (Map<Long, MembershipType>) servletRequest.getAttribute(ENTITLEMENT_CACHE);
        if (tagsag != null) {
            log.debug("Reusing request entitlement cache");

            return tagsag;
        }

        tagsag = new HashMap<Long, MembershipType>();
        servletRequest.setAttribute(ENTITLEMENT_CACHE, tagsag);

        Set<?> entitlementSet =
                (Set<?>) servletRequest.getAttribute(ENTITLEMENT_ATTRNAME);

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
                        MembershipType tt =
                                MembershipType.fromEntitlement(tagsagTipus);
                        if (tt == null) {
                            log.warn("Cannot map entitlement " + tagsagTipus);
                            continue;
                        }
                        MembershipType temp = tagsag.get(csoportId);
                        if (temp != null) {
                            if (temp != MembershipType.KORVEZETO && tt ==
                                    MembershipType.KORVEZETO) {
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

    public User getUserAttributes(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();
        User felhasznalo = new User();

        felhasznalo.setEmailAddress(getSingleValuedStringAttribute(servletRequest, EMAIL_ATTRNAME));
        felhasznalo.setLastName(getSingleValuedStringAttribute(servletRequest, VEZETEKNEV_ATTRNAME));
        felhasznalo.setFirstName(getSingleValuedStringAttribute(servletRequest, KERESZTNEV_ATTRNAME));
        felhasznalo.setNickName(getSingleValuedStringAttribute(servletRequest, BECENEV_ATTRNAME));

        String neptunUrn =
                getSingleValuedStringAttribute(servletRequest, NEPTUN_ATTRNAME);
        
        if (neptunUrn != null) {
            Matcher m = NEPTUN_PATTERN.matcher(neptunUrn);
            if (m.matches()) {
                felhasznalo.setNeptunCode(m.group(1));
            }
        }

        return felhasznalo;
    }

    private String getSingleValuedStringAttribute(HttpServletRequest request, String attrName) {
        Set<?> attrSet = (Set<?>) request.getAttribute(attrName);

        if (attrSet != null) {
            Iterator<?> it = attrSet.iterator();
            if (it.hasNext()) {
                return it.next().toString();
            }
        }

        return null;
    }
}
