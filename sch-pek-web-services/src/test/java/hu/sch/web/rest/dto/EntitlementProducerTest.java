package hu.sch.web.rest.dto;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.domain.user.User;
import hu.sch.services.MembershipManagerLocal;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author tomi
 */
@RunWith(MockitoJUnitRunner.class)
public class EntitlementProducerTest {

    @Mock
    MembershipManagerLocal mock;

    User user = new User();

    @Test
    public void entitleForSingleMembership() {
        Membership ms = createMembership();
        when(mock.findMembershipsForUser(user)).thenReturn(Arrays.asList(ms));

        EntitlementProducer ep = new EntitlementProducer(user, mock);
        assertEquals("urn:geant:niif.hu:sch.bme.hu:entitlement:tag:Kir-Dev:1", ep.createEntitlement());
    }

    @Test
    public void noMembershipMeansNoEntitlement() {
        EntitlementProducer ep = new EntitlementProducer(user, mock);
        assertNull(ep.createEntitlement());
    }

    @Test
    public void everyPostHasItsEntitlement() {
        Membership ms = createMembership();
        addPost(ms, "körvezető");
        addPost(ms, "jani");
        when(mock.findMembershipsForUser(user)).thenReturn(Arrays.asList(ms));

        String memberEntitlement = "urn:geant:niif.hu:sch.bme.hu:entitlement:tag:Kir-Dev:1";
        String leaderEntitlemnet = "urn:geant:niif.hu:sch.bme.hu:entitlement:körvezető:Kir-Dev:1";
        String janiEntitlement = "urn:geant:niif.hu:sch.bme.hu:entitlement:jani:Kir-Dev:1";

        EntitlementProducer ep = new EntitlementProducer(user, mock);
        String entitlement = ep.createEntitlement();

        assertTrue(entitlement.contains(memberEntitlement));
        assertTrue(entitlement.contains(leaderEntitlemnet));
        assertTrue(entitlement.contains(janiEntitlement));
    }

    @Test
    public void semicolonSpeparatesEntitlements() {
        Membership ms = createMembership();
        addPost(ms, "körvezető");
        addPost(ms, "jani");
        when(mock.findMembershipsForUser(user)).thenReturn(Arrays.asList(ms));

        EntitlementProducer ep = new EntitlementProducer(user, mock);
        String entitlement = ep.createEntitlement();

        // entitlements are separated by a semicolon
        assertEquals(3, entitlement.split(";").length);
    }

    @Test
    public void membershipUnderReviewDoesNotHaveMemberRights() {
        Membership ms = createMembership();
        PostType pt = new PostType();
        pt.setPostName("feldolgozás alatt");
        // FIXME: #86/github
        pt.setId(6L);
        addPost(ms, pt);
        when(mock.findMembershipsForUser(user)).thenReturn(Arrays.asList(ms));

        String memberEntitlement = "urn:geant:niif.hu:sch.bme.hu:entitlement:tag:Kir-Dev:1";

        EntitlementProducer ep = new EntitlementProducer(user, mock);
        assertFalse(ep.createEntitlement().contains(memberEntitlement));
    }

    private Membership createMembership() {
        Membership ms = new Membership();
        ms.setGroupId(1L);
        Group g = new Group();
        g.setName("Kir-Dev");
        ms.setGroup(g);
        user.getMemberships().add(ms);
        return ms;
    }

    private void addPost(Membership ms, String postName) {
        PostType pt = new PostType();
        pt.setPostName(postName);
        addPost(ms, pt);
    }

    private void addPost(Membership ms, PostType pt) {
        Post p = new Post();
        p.setPostType(pt);
        ms.getPosts().add(p);
    }
}