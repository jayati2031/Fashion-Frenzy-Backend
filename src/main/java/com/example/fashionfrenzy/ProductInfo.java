package com.example.fashionfrenzy;

public class ProductInfo {
    private String gender, category;
    private String imageSrc, title, brand, price, url;

    public ProductInfo(String gender, String category, String imageSrc, String title, String brand, String price, String url) {
        this.gender = gender;
        this.category = category;
        this.imageSrc = imageSrc;
        this.title = title;
        this.brand = brand;
        this.price = price;
        this.url = url;
    }

    public String getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public String getTitle() {
        return title;
    }

    public String getBrand() {
        return brand;
    }

    public String getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }
}
