package com.skillswap.entities;

public class TutoringService extends Service {
    private String subject;

    public TutoringService(String title, double price, String subject, int providerId) {
        super(title, price, providerId);
        this.subject = subject;
    }

    public TutoringService(int id, String title, double price, String subject, int providerId) {
        super(id, title, price, providerId);
        this.subject = subject;
    }

    @Override
    public String getDetails() { return "Subject: " + subject; }
    public String getSubject() { return subject; }
}