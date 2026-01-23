package com.skillswap.entities;

public abstract class Service {
    protected int id;
    protected String title;
    protected double price;
    protected int providerId;

    public Service(int id, String title, double price, int providerId) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.providerId = providerId;
    }

    public Service(String title, double price, int providerId) {
        this.title = title;
        this.price = price;
        this.providerId = providerId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getProviderId() { return providerId; }

    public abstract String getDetails();
}