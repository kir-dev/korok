/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.MembershipType;
import hu.sch.domain.Post;
import hu.sch.domain.User;
import hu.sch.domain.PostType;
import hu.sch.services.PostManagerLocal;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.PatternHolder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.LengthBetweenValidator;

/**
 *
 * @author aldaris
 */
public final class ChangePost extends SecuredPageTemplate {

    @EJB(name = "PostManagerBean")
    private PostManagerLocal postManager;
    private static Logger log = Logger.getLogger(ChangePost.class);
    private String postName;
    private Boolean isDelegatedPost;

    public ChangePost(final PageParameters params) {
        Long memberId;
        try {
            memberId = new Long(params.getLong("memberid"));
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

        add(new FeedbackPanel("pagemessages"));
        //kell, hogy a csoporttagságok is betöltődjenek
        Group group = userManager.findGroupWithCsoporttagsagokById(ms.getGroup().getId());
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
                        if (temp.getPostType().getPostName().equals(MembershipType.KORVEZETO.toString())) {
                            getSession().error("A körvezetői posztot nem szüntetheted meg, azt csak átruházni lehet egy másik körtagra.");
                            throw new RestartResponseException(ShowGroup.class, new PageParameters("id=" + ms.getGroup().getId()));
                        }
                        removedPosts.add(temp);
                    }
                }
                Iterator<PostType> it = newRights.iterator();
                while (it.hasNext()) {
                    PostType temp = it.next();
                    if (temp.getPostName().equals(MembershipType.KORVEZETO.toString())) {
                        it.remove();
                        try {
                            postManager.changeGroupLeader(ms, temp);
                        } catch (Exception ex) {
                            getSession().error(ex.getCause().getMessage());
                            throw new RestartResponseException(ChangePost.class, new PageParameters("memberid=" + ms.getId()));
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
                    getSession().error("Az új poszt létrehozása közben hiba lépett fel, " +
                            "valószínűleg egy már létező posztot szerettél volna újra felvenni.");
                }
            }
        };

        RequiredTextField<String> postNameTF =
                new RequiredTextField<String>("postNameTF", new PropertyModel<String>(this, "postName"));
        postNameTF.add(new LengthBetweenValidator(2, 30));
        postNameTF.add(new PatternValidator(PatternHolder.groupNameOrPostTypePattern));

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
