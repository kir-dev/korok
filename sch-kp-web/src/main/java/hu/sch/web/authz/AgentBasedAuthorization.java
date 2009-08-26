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

    private static final Logger log =
            Logger.getLogger(AgentBasedAuthorization.class);
    private static final String ENTITLEMENT_CACHE =
            "hu.sch.kp.web.authz.EntitlementCache";
    private static final Pattern VIRID_PATTERN =
            Pattern.compile("^.*:([0-9]+)$");
    private static final Pattern NEPTUN_PATTERN =
            Pattern.compile("^.*:([A-Za-z0-9]{6,7})$");
    private static final Pattern ENTITLEMENT_PATTERN =
            //                          jog:csoportnév:csoportid
            Pattern.compile("^.*:entitlement:([^:]+):([^:]+):([0-9]+)$");
    private static final String ENTITLEMENT_ATTRNAME = "eduPersonEntitlement";
    private static final String VIRID_ATTRNAME = "virid";
    private static final String NEPTUN_ATTRNAME = "neptun";
    private static final String LASTNAME_ATTRNAME = "sn";
    private static final String FIRSTNAME_ATTRNAME = "givenName";
    private static final String NICNKAME_ATTRNAME = "displayName";
    private static final String EMAIL_ATTRNAME = "mail";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Application wicketApplication) {
        log.warn("Agent based authorization mode successfully initiated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getUserid(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();
        Set<String> viridSet = (Set<String>) servletRequest.getAttribute(VIRID_ATTRNAME);

        if (viridSet != null) {
            for (String virid : viridSet) {
                Matcher m = VIRID_PATTERN.matcher(virid);
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

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        Map<Long, MembershipType> memberships = parseEntitlements(wicketRequest);
        MembershipType msType = memberships.get(group.getId());
        if (msType == null) {
            return false;
        }

        //Mivel csak körvezetői tagságokat tárolunk, így ha volt találat, akkor az
        //biztosan körvezetői tagság volt.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupLeaderInSomeGroup(Request wicketRequest) {
        Map<Long, MembershipType> memberships = parseEntitlements(wicketRequest);

        return !memberships.isEmpty();
    }

    /**
     * A kérésben található entitlement-ek feldolgozását elvégző függvény.
     * @param wicketRequest Wicket kérés
     * @return CsoportId-Tagságtípus párok
     */
    @SuppressWarnings("unchecked")
    private Map<Long, MembershipType> parseEntitlements(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();

        Map<Long, MembershipType> memberships =
                (Map<Long, MembershipType>) servletRequest.getAttribute(ENTITLEMENT_CACHE);
        if (memberships != null) {
            log.debug("Reusing request entitlement cache");

            return memberships;
        }

        memberships = new HashMap<Long, MembershipType>();

        Set<String> entitlementSet =
                (Set<String>) servletRequest.getAttribute(ENTITLEMENT_ATTRNAME);

        if (entitlementSet != null) {
            for (String entitlement : entitlementSet) {
                String[] entitlements = entitlement.split("\\|");
                for (String string : entitlements) {
                    Matcher m = ENTITLEMENT_PATTERN.matcher(string);
                    if (m.matches()) {
                        String membershipType = m.group(1);
                        String groupName = m.group(2);
                        Long groupId = Long.parseLong(m.group(3));

                        if (log.isDebugEnabled()) {
                            log.debug("Entitlement: csoportNev: " + groupName +
                                    " ,tagsagTipus: " + membershipType +
                                    " , csoportId: " + groupId.toString());
                        }
                        if (membershipType.equalsIgnoreCase("korvezeto")) {
                            memberships.put(groupId, MembershipType.KORVEZETO);
                        } else {
                            //Az olvashatóság érdekében, csak a körvezetői tagságokkal
                            //foglalkozunk az autorizáció folyamán.
                            continue;
                        }
                    }
                }
            }
        }
        servletRequest.setAttribute(ENTITLEMENT_CACHE, memberships);

        return memberships;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserAttributes(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();
        User user = new User();

        user.setEmailAddress(getSingleValuedStringAttribute(servletRequest, EMAIL_ATTRNAME));
        user.setLastName(getSingleValuedStringAttribute(servletRequest, LASTNAME_ATTRNAME));
        user.setFirstName(getSingleValuedStringAttribute(servletRequest, FIRSTNAME_ATTRNAME));
        user.setNickName(getSingleValuedStringAttribute(servletRequest, NICNKAME_ATTRNAME));

        String neptunUrn =
                getSingleValuedStringAttribute(servletRequest, NEPTUN_ATTRNAME);

        if (neptunUrn != null) {
            Matcher m = NEPTUN_PATTERN.matcher(neptunUrn);
            if (m.matches()) {
                user.setNeptunCode(m.group(1));
            }
        }

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    private String getSingleValuedStringAttribute(HttpServletRequest request, String attrName) {
        Set<String> attrSet = (Set<String>) request.getAttribute(attrName);

        if (attrSet != null) {
            Iterator<String> it = attrSet.iterator();
            if (it.hasNext()) {
                return it.next();
            }
        }

        return null;
    }
}
