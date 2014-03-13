package hu.sch.web.rest.dto;

import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.user.User;
import hu.sch.services.MembershipManagerLocal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author tomi
 */
public class EntitlementProducer {

    private static final String URN_SEPARATOR = ":";
    // github/#88 - auth.sch needs semicolon as the separator, instead of a pipe
    private static final String ENTITLEMENT_SEPARATOR = ";";
    private static final String ENTITLEMENT_PREFIX = "urn:geant:niif.hu:sch.bme.hu:entitlement:";
    private static final String MEMBER = "tag";
    // FIXME: #86 remove magic ids
    private static final Long MEMBERSHIP_PROCESSING_ID = 6L;

    private User user;

    private MembershipManagerLocal membershipManager;

    public EntitlementProducer(User user, MembershipManagerLocal membershipManager) {
        this.user = user;
        this.membershipManager = membershipManager;
    }

    public String createEntitlement() {
        List<String> entitlementStrings = listEntitlements();
        if (entitlementStrings.isEmpty()) {
            return null;
        }
        return StringUtils.join(entitlementStrings, ENTITLEMENT_SEPARATOR);
    }

    private List<String> listEntitlements() {
        List<String> result = new ArrayList<>();
        List<Membership> memberships = membershipManager.findMembershipsForUser(user);

        for (Membership membership : memberships) {
            // for a memberships under review user does not get 'tag' entitlement
            if (!isMembershipUnderReview(membership)) {
                // everybody has the 'tag' entitlement
                result.add(getEntitlementString(membership, MEMBER));
            }

            for (Post post : membership.getPosts()) {
                result.add(getEntitlementString(membership, post.getPostType().getPostName()));
            }
        }
        return result;
    }

    private String getEntitlementString(Membership ms, String post) {
        StringBuilder sb = new StringBuilder();
        sb.append(ENTITLEMENT_PREFIX);
        sb.append(post);
        sb.append(URN_SEPARATOR);
        sb.append(ms.getGroup().getName());
        sb.append(URN_SEPARATOR);
        sb.append(ms.getGroupId());

        return sb.toString();
    }

    private boolean isMembershipUnderReview(Membership ms) {
        for (Post post : ms.getPosts()) {
            if (MEMBERSHIP_PROCESSING_ID.equals(post.getPostType().getId())) {
                return true;
            }
        }

        return false;
    }
}
