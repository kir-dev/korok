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
package hu.sch.web;

import hu.sch.domain.EntrantType;
import hu.sch.domain.Membership;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.kp.group.EditGroupInfo;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.group.ShowGroup;
import hu.sch.web.kp.group.GroupHistory;
import hu.sch.web.kp.logout.Logout;
import hu.sch.web.kp.user.UserHistory;
import hu.sch.web.idm.pages.UserNameReminder;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import hu.sch.web.idm.pages.RegistrationPage;
import hu.sch.web.kp.admin.CreateGroup;
import hu.sch.web.kp.admin.CreateNewPerson;
import hu.sch.web.kp.admin.EditSettings;
import hu.sch.web.kp.admin.ShowInactive;
import hu.sch.web.kp.consider.ConsiderPage;
import hu.sch.web.kp.valuation.Valuations;
import hu.sch.web.kp.valuation.NewValuation;
import hu.sch.web.kp.group.ChangeDelegates;
import hu.sch.web.kp.group.ChangePost;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.search.SearchResultsPage;
import hu.sch.web.kp.svie.SvieAccount;
import hu.sch.web.kp.svie.SvieGroupMgmt;
import hu.sch.web.kp.svie.SvieUserMgmt;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.profile.confirmation.ConfirmPage;
import hu.sch.web.wicket.util.EntrantTypeConverter;
import hu.sch.web.wicket.util.PostTypeConverter;
import hu.sch.web.wicket.util.ValuationStatusConverter;
import org.apache.log4j.Logger;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.lang.PackageName;

/**
 * PhoenixApplication, amelyben a Phoenix arra utal, hogy ez az alkalmazás a VIR
 * hamvaiból éledt újjá, és az idő folyamán a cél egy a régi VIR-hez
 * valamennyire hasonlító közösségi portál megtestesítése.
 *
 * @author aldaris
 * @author hege
 * @author messo
 */
public class KorokApplication extends AbstractPekApplication {

    private static Logger log = Logger.getLogger(KorokApplication.class);

    /**
     * Az alapértelmezett kezdőlap
     * @return A kezdőlap osztálya
     */
    @Override
    public Class<ShowUser> getHomePage() {
        return ShowUser.class;
    }

    @Override
    public void onInitialization() {
        //körök linkek
        mountBookmarkablePage("/showuser", ShowUser.class);
        mountBookmarkablePage("/userhistory", UserHistory.class);
        mountBookmarkablePage("/search", SearchResultsPage.class);
        mountBookmarkablePage("/confirm", ConfirmPage.class);

        mountBookmarkablePage("/showgroup", ShowGroup.class);
        mountBookmarkablePage("/grouphierarchy", GroupHierarchy.class);
        mountBookmarkablePage("/grouphistory", GroupHistory.class);
        mountBookmarkablePage("/editgroupinfo", EditGroupInfo.class);
        mountBookmarkablePage("/changepost", ChangePost.class);

        mountBookmarkablePage("/valuation", Valuations.class);
        mountBookmarkablePage("/valuationdetails", ValuationDetails.class);
        mountBookmarkablePage("/newvaluation", NewValuation.class);

        mountBookmarkablePage("/svieaccount", SvieAccount.class);
        mountBookmarkablePage("/delegates", ChangeDelegates.class);
        mountBookmarkablePage("/consider", ConsiderPage.class);
        mountBookmarkablePage("/administration", EditSettings.class);
        mountBookmarkablePage("/administration/svieusermgmt", SvieUserMgmt.class);
        mountBookmarkablePage("/administration/sviegroupmgmt", SvieGroupMgmt.class);
        mountBookmarkablePage("/showinactive", ShowInactive.class);
        mountBookmarkablePage("/creategroup", CreateGroup.class);
        mountBookmarkablePage("/createperson", CreateNewPerson.class);

        //IDM linkek
        mountBookmarkablePage("/reminder", UserNameReminder.class);
        mount(new HybridUrlCodingStrategy("/register", RegistrationPage.class));
        mountBookmarkablePage("/registerfinished", RegistrationFinishedPage.class);
        mountBookmarkablePage("/logout", Logout.class);

        mount("/error", PackageName.forClass(InternalServerError.class));

//        mountBookmarkablePage("/profile", ShowPersonPage.class);
//        mountBookmarkablePage("profile/edit", EditPage.class);

        //alkalmazás beállítások
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredError.class);
        getPageSettings().setAutomaticMultiWindowSupport(false);

        log.warn("Application has been successfully initiated");
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(EntrantType.class, new EntrantTypeConverter());
        locator.set(ValuationStatus.class, new ValuationStatusConverter());
        locator.set(Membership.class, new PostTypeConverter());

        return locator;
    }
}
