package com.example.toshiba.appchat.Model;

public class Message {
    private String message ,type ,from;
    private boolean seen;
    private long time;

    public Message() {
    }

    public Message(String message, String type, boolean seen, long time ,String from) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from=from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
