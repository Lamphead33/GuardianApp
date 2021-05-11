package com.example.finalproject;

public class Article {
    protected String title;
    protected String url;
    protected String section;

    public Article(String title, String url, String section){
        this.title = title;
        this.url = url;
        this.section = section;
    }

    public String getTitle(){
        return title;
    }

    public String getUrl(){
        return url;
    }

    public String getSection(){
        return section;
    }


}
