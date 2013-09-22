package hu.sch.ejb.search;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttribute;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.domain.user.UserAttribute_;
import hu.sch.domain.user.User_;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Builds the query for complex search.
 *
 * @author tomi
 */
public class SearchQueryBuilder {

    private EntityManager em;
    private final String keyword;
    private Root<User> usr;
    private Join<User, UserAttribute> privAttr;
    private CriteriaBuilder builder;

    public SearchQueryBuilder(EntityManager em, String keyword) {
        this.em = em;
        this.keyword = keyword;
    }

    public TypedQuery<User> build() {
        builder = em.getCriteriaBuilder();
        CriteriaQuery<User> q = builder.createQuery(User.class);

        usr = q.from(User.class);
        privAttr = usr.join(User_.privateAttributes, JoinType.LEFT);

        List<Predicate> andFilters = new ArrayList<>();
        for (String word : keyword.split(" ")) {
            Predicate or = builder.or(
                    buildLikeQueryPart(usr.get(User_.firstName), word),
                    buildLikeQueryPart(usr.get(User_.lastName), word),
                    buildLikeQueryPart(usr.get(User_.nickName), word),
                    buildLikeQueryPart(usr.get(User_.screenName), word),
                    buildEmailQueryPart(word),
                    buildRoomNumberQueryPart(word)
                );

            andFilters.add(or);
        }

        q.where(andFilters.toArray(new Predicate[andFilters.size()]));
        q.distinct(true);
        return em.createQuery(q);
    }

    private Predicate buildLikeQueryPart(Expression<String> expr, String word) {
        return builder.like(builder.lower(expr), buildLikeString(word));
    }

    private String buildLikeString(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument 'word' cannot be null");
        }
        return "%".concat(word.toLowerCase()).concat("%");
    }

    private Predicate buildEmailQueryPart(String word) {
        return builder.and(
                // email is visible
                builder.equal(privAttr.get(UserAttribute_.attrName), UserAttributeName.EMAIL),
                builder.isTrue(privAttr.get(UserAttribute_.visible)),
                // and equals to the given word
                builder.equal(usr.get(User_.emailAddress), word));
    }

    private Predicate buildRoomNumberQueryPart(String word) {
        return builder.and(
                // roomnumber is visible
                builder.equal(privAttr.get(UserAttribute_.attrName), UserAttributeName.ROOM_NUMBER),
                builder.isTrue(privAttr.get(UserAttribute_.visible)),
                // room number consists of [dormitor] [room]
                builder.or(
                    builder.like(usr.get(User_.dormitory), buildLikeString(word)),
                    builder.like(usr.get(User_.room), buildLikeString(word))
                )
            );
    }
}
