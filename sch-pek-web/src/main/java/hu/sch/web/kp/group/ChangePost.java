package hu.sch.web.kp.group;

import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.domain.util.PatternHolder;
import hu.sch.web.kp.KorokPage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public final class ChangePost extends KorokPage {

    private static Logger log = LoggerFactory.getLogger(ChangePost.class);
    private String postName;
    private Boolean isDelegatedPost;

    public ChangePost(final PageParameters params) {
        Long memberId;
        try {
            memberId = params.get("memberid").toLong();
        } catch (StringValueConversionException svce) {
            error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        final Membership ms = membershipManager.findMembership(memberId);
        if (ms == null) {
            error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Jog megadása");

        //kell, hogy a csoporttagságok is betöltődjenek
        Group group = ms.getGroup();
        membershipManager.fetchMembershipsFor(group);
        User user = ms.getUser();

        if (!isUserGroupLeader(group) && !hasUserDelegatedPostInGroup(group)) {
            getSession().error("Nincs jogod a megadott művelethez");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        if (user == null) {
            getSession().error("Hibás adatok");
            throw new RestartResponseException(ShowGroup.class, new PageParameters().add("id", group.getId()));
        }
        add(new Label("groupname", group.getName()));
        add(new Label("username", user.getFullName()));
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
                            throw new RestartResponseException(ShowGroup.class, new PageParameters().add("id", ms.getGroup().getId()));
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
                                throw new RestartResponseException(ChangePost.class, new PageParameters().add("memberid", ms.getId()));
                            }
                        } else {
                            log.warn("A következő felhasználó: " + getUser().getId() + " megpróbált a delegált posztjával körvezetővé válni, "
                                    + "vagy a körvezető személyét valaki másra megváltoztatni! A kezdeményezett fél: " + ms.getUser().getId());
                            getSession().error("Ez most nem volt szép Tőled, nemsokára jön is érted a fekete kocsi");
                            throw new RestartResponseException(ShowGroup.class, new PageParameters().add("id", ms.getGroup().getId()));
                        }
                        break;
                    }
                }

                postManager.setPostsForMembership(ms, removedPosts, newRights);
                getSession().info("A beállítások sikeresen mentésre kerültek");
                setResponsePage(ShowGroup.class, new PageParameters().add("id", ms.getGroup().getId()));
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
                } else {
                    getSession().error("Az új poszt létrehozása közben hiba lépett fel, "
                            + "valószínűleg egy már létező posztot szerettél volna újra felvenni.");
                }
            }
        };

        RequiredTextField<String> postNameTF =
                new RequiredTextField<String>("postNameTF", new PropertyModel<String>(this, "postName"));
        postNameTF.add(StringValidator.lengthBetween(2, 30));
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
