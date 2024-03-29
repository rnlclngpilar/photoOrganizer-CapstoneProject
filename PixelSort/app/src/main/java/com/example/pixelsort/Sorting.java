package com.example.pixelsort;

public class Sorting {
    private String sorting_id;
    private String year;
    private String month;
    private String day;
    private String thumbnail;
    private String key;
    private String keyword;

    public Sorting() {}

    public Sorting(String sorting_id, String year, String thumbnail) {
        this.sorting_id = sorting_id;
        this.year = year;
        this.thumbnail = thumbnail;
    }

    public String getSorting_id() {
        return sorting_id;
    }

    public void setSorting_id(String sorting_id) {
        this.sorting_id = sorting_id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
