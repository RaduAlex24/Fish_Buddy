package com.example.licenta.virtualAssistant.clase;

public class Message {
    private String content;
    private boolean isReceived;


    // Constructori
    public Message(String content, boolean isReceived) {
        this.content = content;
        this.isReceived = isReceived;
    }


    // Getteri si setteri
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }


    // Metode
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("message='").append(content).append('\'');
        sb.append(", isReceived=").append(isReceived);
        sb.append('}');
        return sb.toString();
    }
}
