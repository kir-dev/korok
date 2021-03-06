package hu.sch.web.common;

import hu.sch.domain.SpotImage;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.NotImplementedException;
import hu.sch.web.wicket.components.ImageResource;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 *
 * @author messo
 * @since 2.4
 */
class RecommendedPhotoPanel extends Panel {

    @Inject
    UserManagerLocal userManager;

    public RecommendedPhotoPanel(String contentId, final String userUid, final User user) {
        super(contentId);

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        final SpotImage si = userManager.getSpotImage(user);

        Fragment fragment = new Fragment("fragment", "prompt", null, null);
        container.add(fragment);

        Form<?> form = new Form("form");
        fragment.add(form);

        NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel<ImageResource>() {

            @Override
            public ImageResource getObject() {
                // TODO: read image from filesystem
                // return new ImageResource(si.getImage(), "png");
                throw new NotImplementedException("TODO: read image from file system.");
            }
        });

        fragment.add(photo);

        form.add(new AjaxButton("accept") {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // elfogadtuk
                if (userManager.acceptRecommendedPhoto(userUid)) {
                    container.replace(new Fragment("fragment", "accepted", null, null));
                } else {
                    container.replace(new Fragment("fragment", "failed", null, null));
                }
                target.add(container);
            }
        });

        form.add(new AjaxButton("decline") {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                userManager.declineRecommendedPhoto(user);
                container.replace(new Fragment("fragment", "declined", null, null));
                target.add(container);
            }
        });
    }
}
