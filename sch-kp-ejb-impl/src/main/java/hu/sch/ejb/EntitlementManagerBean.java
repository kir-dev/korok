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
package hu.sch.ejb;

import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.services.EntitlementManagerRemote;
import hu.sch.services.UserManagerLocal;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "EntitlementManager")
public class EntitlementManagerBean implements EntitlementManagerRemote {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;
    private static final long serialVersionUID = 1L;

    public User createUserEntry(User user) {
        if (user.getNeptunCode() != null) {
            Query q = em.createNamedQuery(User.findUserByNeptunCode);
            q.setParameter("neptun", user.getNeptunCode());
            try {
                User exists = (User) q.getSingleResult();
                return mapReturn(exists);
            } catch (Exception e) {
            }
        }
        User newUser = mapNew(user);
        em.persist(newUser);
        em.flush();
        return mapReturn(newUser);
    }

    protected User mapReturn(User f) {
        User felhasznalo = new User();
        felhasznalo.setId(f.getId());
        felhasznalo.setNeptunCode(f.getNeptunCode());
        felhasznalo.setEmailAddress(f.getEmailAddress());

        return felhasznalo;
    }

    protected User mapNew(User f) {
        User user = mapReturn(f);
        user.setNeptunCode(f.getNeptunCode());
        user.setLastName(f.getLastName());
        user.setFirstName(f.getFirstName());
        user.setNickName(f.getNickName());
        user.setEmailAddress(f.getEmailAddress());
        user.setSvieMembershipType(SvieMembershipType.NEMTAG);
        user.setSvieStatus(SvieStatus.NEMTAG);

        return user;
    }

    public User findUser(String neptun, String email) {
        return mapReturn((User) em.createNamedQuery(User.findUser).
                setParameter("neptunkod", neptun).
                setParameter("emailcim", email).
                getSingleResult());
    }

    public User findUser(Long virId) {
        return mapReturn(em.find(User.class, virId));
    }
}
