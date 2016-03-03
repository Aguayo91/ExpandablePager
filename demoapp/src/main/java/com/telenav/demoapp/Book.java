package com.telenav.demoapp;

public class Book {
    private String title;
    private String author;
    private String description;
    private String url;

    public Book(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Book setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Book setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Book setUrl(String url) {
        this.url = url;
        return this;
    }
}
