package hu.sch.web.rest.dto;

/**
 *
 * @author balo
 */
public class MembershipResult {

    private final long groupId;
    private final String groupName;
    private final boolean isLeader;

    public MembershipResult(final long groupId, final String groupName,
            final boolean isLeader) {

        this.groupId = groupId;
        this.groupName = groupName;
        this.isLeader = isLeader;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isIsLeader() {
        return isLeader;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.groupId ^ (this.groupId >>> 32));
        hash = 29 * hash + (this.groupName != null ? this.groupName.hashCode() : 0);
        hash = 29 * hash + (this.isLeader ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MembershipResult other = (MembershipResult) obj;
        if (this.groupId != other.groupId) {
            return false;
        }
        if ((this.groupName == null) ? (other.groupName != null) : !this.groupName.equals(other.groupName)) {
            return false;
        }
        if (this.isLeader != other.isLeader) {
            return false;
        }
        return true;
    }
}
