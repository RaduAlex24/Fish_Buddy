package com.example.licenta.virtualAssistant.clase;

public class Message {
    private String message;
    private boolean isReceived;


    // Constructori
    public Message(String message, boolean isReceived) {
        this.message = message;
        this.isReceived = isReceived;
    }


    // Getteri si setteri
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }


    // Metode
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("message='").append(message).append('\'');
        sb.append(", isReceived=").append(isReceived);
        sb.append('}');
        return sb.toString();
    }
}
