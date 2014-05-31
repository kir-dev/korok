package hu.sch.api.user;

import hu.sch.domain.user.User;

public class AvatarView {

    private final String domain;
    private final User user;

    public AvatarView(User user, String domain) {
        this.user = user;
        this.domain = domain;
    }

    public String getPath() {
        StringBuilder builder = new StringBuilder("//");
        builder.append(domain);
        builder.append("/");
        builder.append(user.getPhotoPath());

        return builder.toString();
    }

}
