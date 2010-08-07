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

package hu.sch.web.kp.group;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.User;
import hu.sch.domain.PostType;
import hu.sch.web.kp.KorokPage;
import hu.sch.domain.util.PatternHolder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.IClusterable;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.LengthBetweenValidator;

/**
 *
 * @author aldaris
 */
public final class ChangePost extends KorokPage {

    private static Logger log = Logger.getLogger(ChangePost.class);
    private String postName;
    private Boolean isDelegatedPost;

    public ChangePost(final PageParameters params) {
        Long memberId;
        try {
            memberId = Long.valueOf(params.getLong("memberid"));
        } catch (StringValueConversionException svce) {
            error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        final Membership ms = userManager.getMembership(memberId);
        if (ms == null) {
            error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Jog megadása");

        //kell, hogy a csoporttagságok is betöltődjenek
        Group group = ms.getGroup();
        userManager.loadMemberships(group);
        User user = ms.getUser();

        if (!isUserGroupLeader(group) && !hasUserDelegatedPostInGroup(group)) {
            getSession().error("Nincs jogod a megadott művelethez");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        if (user == null) {
            getSession().error("Hibás adatok");
            throw new RestartResponseException(ShowGroup.class, new PageParameters("id=" + group.getId()));
        }
        add(new Label("groupname", group.getName()));
        add(new Label("username", user.getName()));
        final Input input = new Input(ms);
        setDefaultModel(new CompoundPropertyModel<Input>(input));

        Form form = new Form("changePost") {

            @Override
            protected void onSubmit() {
                //A formon bevitt adatok
                List<PostType> newRights = input.getChoices();
                //Az eltávolítandó posztok
                List<Post> removedPosts = new ArrayList<Post>();
                //A körtag eredeti posztjai
                List<Post> posts = ms.getPosts();
                Iterator<Post> iterator = posts.iterator();
                while (iterator.hasNext()) {
                    Post temp = iterator.next();
                    if (newRights.contains(temp.getPostType())) {
                        newRights.remove(temp.getPostType());
                    } else {
                        if (temp.getPostType().getPostName().equals(PostType.KORVEZETO)) {
                            getSession().error("A körvezetői posztot nem szüntetheted meg, azt csak átruházni lehet egy másik körtagra.");
                            throw new RestartResponseException(ShowGroup.class, new PageParameters("id=" + ms.getGroup().getId()));
                        }
                        removedPosts.add(temp);
                    }
                }
                Iterator<PostType> it = newRights.iterator();
                while (it.hasNext()) {
                    PostType temp = it.next();
                    if (temp.getPostName().equals(PostType.KORVEZETO)) {
                        it.remove();
                        if (isUserGroupLeader(ms.getGroup())) {
                            try {
                                postManager.changeGroupLeader(ms, temp);
                            } catch (Exception ex) {
                                getSession().error(ex.getCause().getMessage());
                                throw new RestartResponseException(ChangePost.class, new PageParameters("memberid=" + ms.getId()));
                            }
                        } else {
                            log.warn("A következő felhasználó: " + getUser().getId() + " megpróbált a delegált posztjával körvezetővé válni, "
                                    + "vagy a körvezető személyét valaki másra megváltoztatni! A kezdeményezett fél: " + ms.getUser().getId());
                            getSession().error("Ez most nem volt szép Tőled, nemsokára jön is érted a fekete kocsi");
                            throw new RestartResponseException(ShowGroup.class, new PageParameters("id=" + ms.getGroup().getId()));
                        }
                        break;
                    }
                }

                postManager.setPostsForMembership(ms, removedPosts, newRights);
                getSession().info("A beállítások sikeresen mentésre kerültek");
                setResponsePage(ShowGroup.class, new PageParameters("id=" + ms.getGroup().getId()));
            }
        };

        List<PostType> postTypes = postManager.getAvailablePostTypesForGroup(group);

        CheckBoxMultipleChoice<PostType> multipleChoice =
                new CheckBoxMultipleChoice<PostType>("choices", postTypes);
        multipleChoice.setChoiceRenderer(new IChoiceRenderer<PostType>() {

            @Override
            public Object getDisplayValue(PostType object) {
                if (object.getDelegatedPost()) {
                    return object.toString() + " (delegált)";
                } else {
                    return object.toString();
                }
            }

            @Override
            public String getIdValue(PostType object, int index) {
                return object.toString();
            }
        });

        form.add(multipleChoice);
        add(form);

        Form<Void> createPostTypeForm = new Form<Void>("postTypeForm") {

            @Override
            protected void onSubmit() {
                if (log.isDebugEnabled()) {
                    log.debug("Creating new posttype (" + postName + ") for group: " + ms.getGroup());
                }

                if (postManager.createPostType(postName, ms.getGroup(), isDelegatedPost)) {
                    getSession().info("Az új poszt sikeresen elkészült.");
                    setResponsePage(ChangePost.class, params);
                    return;
                } else {
                    getSession().error("Az új poszt létrehozása közben hiba lépett fel, "
                            + "valószínűleg egy már létező posztot szerettél volna újra felvenni.");
                }
            }
        };

        RequiredTextField<String> postNameTF =
                new RequiredTextField<String>("postNameTF", new PropertyModel<String>(this, "postName"));
        postNameTF.add(new LengthBetweenValidator(2, 30));
        postNameTF.add(new PatternValidator(PatternHolder.GROUP_NAME_OR_POSTTYPE_PATTERN));

        CheckBox delegatedBox = new CheckBox("delegatedBox", new PropertyModel<Boolean>(this, "isDelegatedPost"));
        createPostTypeForm.add(delegatedBox);
        createPostTypeForm.add(postNameTF);
        add(createPostTypeForm);
    }

    private class Input implements IClusterable {

        List<PostType> choices = new ArrayList<PostType>();
        Membership cst = null;

        public Input(Membership ms) {
            List<Post> posts = postManager.getCurrentPostsForGroup(ms);

            for (Post post : posts) {
                choices.add(post.getPostType());
            }
        }

        public List<PostType> getChoices() {
            return choices;
        }

        public void setChoices(List<PostType> temp) {
            choices = temp;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (PostType postType : choices) {
                sb.append(postType.toString());
            }
            return sb.toString();
        }
    }
}
