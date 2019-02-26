package com.example.liorkaramany.swaiterbeta;

import java.io.Serializable;

public class Dish implements Serializable {
    public String id;
    public String name;
    public double price;
    public String url;

    public Dish(String id, String name, double price, String url)
    {
        this.id=id;
        this.name=name;
        this.price=price;
        this.url = url;
    }

    public Dish() {}

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name+" "+price+".";
    }
}

