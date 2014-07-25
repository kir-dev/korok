package hu.sch.web.dev;

import java.io.Serializable;

public class DevSettingsFormModel implements Serializable {

    private Long userId;

    public DevSettingsFormModel() {
        this(18925L);
    }

    public DevSettingsFormModel(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
