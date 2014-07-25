package hu.sch.services.dto;

import com.google.gson.annotations.SerializedName;

public class OAuthDormitory {

    @SerializedName("buildingName")
    private String buildingName;

    @SerializedName("roomNumber")
    private String room;

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
