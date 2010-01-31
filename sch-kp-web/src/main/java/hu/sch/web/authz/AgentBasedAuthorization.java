/**
 * Copyright (c) 2009, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.PostType;
import hu.sch.domain.util.PatternHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebRequest;

/**
 * Autorizációs feladatok ellátásáért felelős osztály, mely feldolgozza az
 * agenttől kapott adatokat, melyeket az egyes oldalak később le tudnak
 * kérdezni.
 *
 * @author hege
 */
@SuppressWarnings("unchecked")
public final class AgentBasedAuthorization implements UserAuthorization {

    /**
     * Logoláshoz szükséges objektum
     */
    private static final Logger log =
            Logger.getLogger(AgentBasedAuthorization.class);
    /**
     * A HTTP headerben ezen kulcs alá kerül tárolásra a cache-elt autorizációs
     * adatok
     */
    private static final String ENTITLEMENT_CACHE =
            "hu.sch.kp.web.authz.EntitlementCache";
    /**
     * A csoporttagsági adatokhoz tartozó HTTP header kulcs
     */
    private static final String ENTITLEMENT_ATTRNAME = "eduPersonEntitlement";
    /**
     * A VIRID-hez tartozó HTTP header kulcs
     */
    private static final String VIRID_ATTRNAME = "virid";
    /**
     * A neptun kódhoz tartozó HTTP header kulcs
     */
    private static final String NEPTUN_ATTRNAME = "neptun";
    /**
     * A vezetéknévhez tartozó HTTP header kulcs
     */
    private static final String LASTNAME_ATTRNAME = "sn";
    /**
     * A keresztnévhez tartozó HTTP header kulcs
     */
    private static final String FIRSTNAME_ATTRNAME = "givenName";
    /**
     * A becenévhez tartozó HTTP header kulcs
     */
    private static final String NICNKAME_ATTRNAME = "displayName";
    /**
     * Az e-mailhez tartozó HTTP header kulcs
     */
    private static final String EMAIL_ATTRNAME = "mail";
    /**
     * A csoporttagság stringben található elválasztó karakter
     */
    private static final String ENTITLEMENT_SEPARATOR = "\\|";

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
                Matcher m = PatternHolder.VIRID_PATTERN.matcher(virid);
                if (m.matches()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Parsed virid from agent: " + m.group(1));
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
            log.debug("Check container role: " + role + ": " + inRole);
        }

        return inRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        List<Long> memberships = parseEntitlements(wicketRequest);
        if (!memberships.contains(group.getId())) {
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
        List<Long> memberships = parseEntitlements(wicketRequest);

        return !memberships.isEmpty();
    }

    /**
     * A kérésben található entitlementek feldolgozását elvégző függvény.
     * @param wicketRequest Wicket kérés
     * @return CsoportId-Tagságtípus párok
     */
    private List<Long> parseEntitlements(Request wicketRequest) {
        HttpServletRequest servletRequest =
                ((WebRequest) wicketRequest).getHttpServletRequest();

        List<Long> memberships =
                (List<Long>) servletRequest.getAttribute(ENTITLEMENT_CACHE);
        if (memberships != null) {
            log.debug("Reusing request entitlement cache");

            return memberships;
        }

        memberships = new ArrayList<Long>();

        Set<String> entitlementSet =
                (Set<String>) servletRequest.getAttribute(ENTITLEMENT_ATTRNAME);

        if (entitlementSet != null) {
            for (String entitlement : entitlementSet) {
                String[] entitlements = entitlement.split(ENTITLEMENT_SEPARATOR);
                for (String string : entitlements) {
                    Matcher m = PatternHolder.ENTITLEMENT_PATTERN.matcher(string);
                    if (m.matches()) {
                        String postType = m.group(1);
                        String groupName = m.group(2);
                        Long groupId = Long.parseLong(m.group(3));

                        if (log.isDebugEnabled()) {
                            log.debug("Entitlement: csoportNev: " + groupName
                                    + " , tagsagTipus: " + postType
                                    + " , csoportId: " + groupId.toString());
                        }
                        if (postType.equalsIgnoreCase(PostType.KORVEZETO)) {
                            memberships.add(groupId);
                        } else {
                            //Az olvashatóság érdekében. Csak a körvezetői tagságokkal
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
            Matcher m = PatternHolder.NEPTUN_PATTERN.matcher(neptunUrn);
            if (m.matches()) {
                user.setNeptunCode(m.group(1));
            }
        }

        return user;
    }

    /**
     * {@inheritDoc}
     */
    private String getSingleValuedStringAttribute(HttpServletRequest request, String attrName) {
        Set<String> attrSet = (Set<String>) request.getAttribute(attrName);

        if (attrSet != null) {
            for (String string : attrSet) {
                return string;
            }
        }

        return null;
    }
}
