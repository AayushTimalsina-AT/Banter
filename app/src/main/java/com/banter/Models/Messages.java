package com.banter.Models;

public class Messages {
    String uId, Message, MessageId;
    Long timestamp;

//    public Messages(String uId, String message, Long timestamp) {
//        this.uId = uId;
//        this.Message = message;
//        this.timestamp = timestamp;
//    }

    public Messages(String uId, String message) {
        this.uId = uId;
        this.Message = message;
    }

    public Messages() {

    }


    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public void remove(int position) {
    }
}
