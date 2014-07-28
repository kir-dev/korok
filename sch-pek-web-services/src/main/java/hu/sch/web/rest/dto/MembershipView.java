package hu.sch.web.rest.dto;

import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class MembershipView {

    private final List<Properties> props;

    public MembershipView(List<Membership> memberships) {
        this.props = buildPropertiesList(memberships);
    }

    public boolean isSuccess() {
        return true;
    }

    @JsonProperty("memberships")
    public List<Properties> getMembershipProperties() {
        return props;
    }

    private List<Properties> buildPropertiesList(List<Membership> memberships) {
        List<Properties> ps = new ArrayList<>();
        for (Membership ms : memberships) {
            ps.add(new Properties(ms));
        }

        return Collections.unmodifiableList(ps);
    }

    public static class Properties {

        // FIXME: #86 remove magic ids
        private static final Long MEMBERSHIP_UNDER_PROCESSING_ID = 6L;

        private final Membership membership;

        private Properties(Membership ms) {
            this.membership = ms;
        }

        public Date getStart() {
            return membership.getStart();
        }

        public Date getEnd() {
            return membership.getEnd();
        }

        public List<String> getPosts() {
            List<String> posts = new ArrayList<>();
            boolean underProcessing = false;

            for (Post post : membership.getPosts()) {
                if (!MEMBERSHIP_UNDER_PROCESSING_ID.equals(post.getPostType().getId())) {
                    posts.add(post.getPostType().getPostName());
                } else {
                    underProcessing = true;
                }
            }

            if (getEnd() != null) {
                posts.add("öregtag");
            } else if (!underProcessing) {
                posts.add("tag");
            }

            return Collections.unmodifiableList(posts);
        }

        @JsonProperty("group_name")
        public String getGroupName() {
            return membership.getGroup().getName();
        }

        @JsonProperty("group_id")
        public Long getGroupId() {
            return membership.getGroupId();
        }
    }

}
