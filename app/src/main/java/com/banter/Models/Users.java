package com.banter.Models;

public class Users {
    String ProfilePic;
    String UserName;
    String Email;
    String Password;
    String UserId;
    String LastMessage;
    String about;
    String LatestMessageTimestamp;

    public String getLatestMessageTimestamp() {
        return LatestMessageTimestamp;
    }

    public void setLatestMessageTimestamp(String latestMessageTimestamp) {
        LatestMessageTimestamp = latestMessageTimestamp;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    String FCMToken;


    public Users(String profilePic, String userName, String email, String password, String userId, String lastMessage) {
        ProfilePic = profilePic;
        UserName = userName;
        Email = email;
        Password = password;
        UserId = userId;
        LastMessage = lastMessage;
    }
    public Users (){}
    //SignUp Constructor
    public Users( String userName, String email, String password) {
        UserName = userName;
        Email = email;
        Password = password;
    }



    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }
}



