package hu.sch.ejb.search;

import hu.sch.domain.user.User;
import hu.sch.domain.user.User_;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
    private CriteriaBuilder builder;

    public SearchQueryBuilder(EntityManager em, String keyword) {
        this.em = em;
        this.keyword = keyword;
        this.builder = em.getCriteriaBuilder();
    }

    public TypedQuery<User> build() {
        CriteriaQuery<User> q = builder.createQuery(User.class);
        usr = q.from(User.class);
        prepareQuery(q);

        return em.createQuery(q);
    }

    public TypedQuery<Long> buildForCount() {
        CriteriaQuery<Long> q = builder.createQuery(Long.class);
        usr = q.from(User.class);
        q.select(builder.count(usr));
        prepareQuery(q);

        return em.createQuery(q);
    }

    private void prepareQuery(CriteriaQuery<?> q) {
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
                // and equals to the given word
                builder.equal(usr.get(User_.emailAddress), word));
    }

    private Predicate buildRoomNumberQueryPart(String word) {
        return builder.and(
                // room number consists of [dormitor] [room]
                builder.or(
                        buildLikeQueryPart(usr.get(User_.dormitory), buildLikeString(word)),
                        buildLikeQueryPart(usr.get(User_.room), buildLikeString(word))
                )
        );
    }
}
