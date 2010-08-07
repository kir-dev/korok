/**
 * Copyright (c) 2009-2010, Peter Major
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
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Fejlesztői teszt autorizációs modul. Lényegében meghazudja nekünk, hogy mely
 * körökben vagyunk körvezetők.
 * 
 * @author hege
 */
public final class DummyAuthorization implements UserAuthorization {

    /**
     * A logoláshoz szükséges logger.
     */
    private static Logger log = Logger.getLogger(DummyAuthorization.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Application wicketApplication) {
        if (wicketApplication.getConfigurationType().equals(WebApplication.DEPLOYMENT)) {
            throw new IllegalStateException("Do not use dummy authz module in production environment!");
        }
        log.warn("Dummy authorization mode initiated successfully");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getUserid(Request wicketRequest) {
        return 18925L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemoteUser(Request wicketRequest) {
        return "konvergal";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        // Kir-Dev, Teaház és 17.szint körvezetői tagsága
        if ((group.getId().equals(331L) || group.getId().equals(106L)
                || group.getId().equals(21L) || group.getId().equals(369L)) || group.getId().equals(26L)) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAbstractRole(Request wicketRequest, String role) {
        if (role.equals("ADMIN")) {
            return true;
        } else if (role.equals("JETI")) {
            return true;
        } else if (role.equals("SVIE")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupLeaderInSomeGroup(Request wicketRequest) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserAttributes(Request wicketRequest) {
        return null;
    }
}
