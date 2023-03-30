package com.example.igclone.Models;

public class Mess {
    private String messageId;
    private String senderId;
    private String message;
    private String avatar;
    private String imgUrl;

    public Mess(String messageId, String senderId, String message, String avatar, String imgUrl) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.avatar = avatar;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Mess() {
    }


    public Mess(String messageId, String senderId, String message, String avatar) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
