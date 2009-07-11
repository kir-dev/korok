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

    public void init(Application wicketApplication) {
        log.warn("Agent based authorization mode successfully initiated.");
    }

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

    public boolean hasRoleInGroup(Request wicketRequest, Group group, MembershipType membershipType) {
        Map<Long, MembershipType> memberships = parseEntitlements(wicketRequest);
        MembershipType msType = memberships.get(group.getId());
        if (msType == null) {
            return false;
        }

        return msType.equals(membershipType);
    }

    public boolean hasRoleInSomeGroup(Request wicketRequest, MembershipType membershipType) {
        Map<Long, MembershipType> memberships = parseEntitlements(wicketRequest);

        return memberships.values().contains(membershipType);
    }

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
        servletRequest.setAttribute(ENTITLEMENT_CACHE, memberships);

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
                        MembershipType mst =
                                MembershipType.fromEntitlement(membershipType);
                        if (mst == null) {
                            log.warn("Cannot map entitlement " + membershipType);
                            continue;
                        }
                        MembershipType temp = memberships.get(groupId);
                        if (temp != null) {
                            if (temp != MembershipType.KORVEZETO && mst ==
                                    MembershipType.KORVEZETO) {
                                memberships.put(groupId, mst);
                            }
                        } else {
                            //FIXME: itt bajok lehetnek azzal, hogy csak a körvezetői
                            //tagságok vannak tárolva, egy Map<groupId, List<mst>>
                            //megoldaná valószínűleg a problémát.
                            memberships.put(groupId, mst);
                        }
                    }
                }
            }
        }

        return memberships;
    }

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
