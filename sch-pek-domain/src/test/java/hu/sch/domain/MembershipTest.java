package hu.sch.domain;

import java.util.Date;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;

/**
 *
 * @author tomi
 */
public class MembershipTest {
    private static final String TEST_POST_NAME = "test";

    private Membership membership;

    @Before
    public void createMembership() {
        membership = new Membership();
    }

    @Test
    public void noPostMeansRegularMember() {
        assertThat(membership.getAllPosts()).contains(Membership.ACTIVE_MEMBERSHIP_POST);
    }

    @Test
    public void endedMembershipHasOregtagPost() {
        membership.setEnd(new Date());
        assertThat(membership.getAllPosts()).contains(Membership.INACTIVE_MEMBERSHIP_POST);
    }

    @Test
    public void regularMembershipOnlyAppliesWhenThereAreNoOtherPosts() {
        addPostToMembership(TEST_POST_NAME);
        assertThat(membership.getAllPosts()).doesNotContain(Membership.ACTIVE_MEMBERSHIP_POST);
    }

    @Test
    public void inactiveMembershipStillHasItsPosts() {
        addPostToMembership(TEST_POST_NAME);
        membership.setEnd(new Date());

        assertThat(membership.getAllPosts()).contains(TEST_POST_NAME);
    }

    @Test
    public void membershipCanHaveMultiplePosts() {
        addPostToMembership(TEST_POST_NAME);
        addPostToMembership(TEST_POST_NAME + "2");

        assertThat(membership.getAllPosts()).hasSize(2);
    }

    private void addPostToMembership(String name) {
        PostType pt = new PostType();
        pt.setPostName(name);
        Post p = new Post();
        p.setPostType(pt);
        membership.getPosts().add(p);
    }
}
