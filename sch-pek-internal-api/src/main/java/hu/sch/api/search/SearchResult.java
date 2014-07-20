package hu.sch.api.search;

import hu.sch.api.group.GroupView;
import hu.sch.api.user.UserView;
import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.toList;

public class SearchResult {

    private final long countOfUsers;
    private final long countOfGroups;
    private final List<UserView> users;
    private final List<GroupView> groups;

    protected SearchResult(long countOfUsers, long countOfGroups, List<UserView> users, List<GroupView> groups) {
        this.countOfUsers = countOfUsers;
        this.countOfGroups = countOfGroups;
        this.users = users;
        this.groups = groups;
    }

    public static SearchResult fromUsers(long userCount, long groupCount, List<User> users) {
        List<UserView> userViewList = users.stream().map(u -> new UserView(u)).collect(toList());
        return new SearchResult(userCount, groupCount, userViewList, Collections.emptyList());
    }

    public static SearchResult fromGroups(long userCount, long groupCount, List<Group> groups) {
        List<GroupView> groupViewList = groups.stream().map(g -> new GroupView(g)).collect(toList());
        return new SearchResult(userCount, groupCount, Collections.emptyList(), groupViewList);
    }

    public long getCountOfUsers() {
        return countOfUsers;
    }

    public long getCountOfGroups() {
        return countOfGroups;
    }

    public List<UserView> getUsers() {
        return users;
    }

    public List<GroupView> getGroups() {
        return groups;
    }

}
