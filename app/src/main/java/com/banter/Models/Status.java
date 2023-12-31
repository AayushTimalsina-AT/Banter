package com.banter.Models;

public class Status {
    String ProfilePic, userName , status, statusId, userId;
    Long timestamp;
    public Status(String profilePic, String userName, String userId,String status, Long timestamp) {
        this.ProfilePic = profilePic;
        this.userName = userName;
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Status(String profilePic, String userName, String status, String statusId,String userId, Long timestamp) {
       this.ProfilePic = profilePic;
        this.userName = userName;
        this.userId = userId;
        this.status = status;
        this.statusId = statusId;
        this.timestamp = timestamp;
        this.userId = userId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Status(){}

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


}
