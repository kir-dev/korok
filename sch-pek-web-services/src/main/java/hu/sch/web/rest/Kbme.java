/**
 * Copyright (c) 2008-2010, Peter Major
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
package hu.sch.web.rest;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.domain.rest.PointInfo;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author aldaris
 */
@Path("/kbme")
@ManagedBean
public class Kbme {

    private static Logger logger = Logger.getLogger(Kbme.class);
    @EJB
    UserManagerLocal userManager;
    @EJB
    ValuationManagerLocal valuationManager;
    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/parent")
    public Group getParentGroups(@QueryParam("id") Long id) {
        doAudit();
        return userManager.getParentGroups(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/child")
    public List<Group> getChildGroups(@QueryParam("id") Long id) {
        doAudit();
        return userManager.getChildGroups(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/leader")
    public User getLeader(@QueryParam("id") Long id) {
        doAudit();
        return userManager.getGroupLeaderForGroup(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/points")
    public List<PointInfo> getPointsForUser(@QueryParam("uid") String uid, @QueryParam("sid") String sid) {
        doAudit();
        return valuationManager.getPointInfoForUid(uid, new Semester(sid));
    }

    private void doAudit() {
        StringBuilder auditMessage = new StringBuilder("AUDIT LOG for GET method. ");
        auditMessage.append(" URL: ");
        if (context != null && context.getRequestUri() != null) {
            auditMessage.append(context.getRequestUri().toString());
        } else {
            logger.info("URIContext or RequestUri was null.");
            auditMessage.append("UNKNOWN");
        }
        logger.info(auditMessage.toString());
    }
}
