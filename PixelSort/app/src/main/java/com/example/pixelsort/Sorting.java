package com.example.pixelsort;

public class Sorting {
    private String sorting_id;
    private String sorting_name;
    private String thumbnail;
    private String key;

    public Sorting() {}

    public Sorting(String sorting_id, String sorting_name, String thumbnail) {
        this.sorting_id = sorting_id;
        this.sorting_name = sorting_name;
        this.thumbnail = thumbnail;
    }

    public String getSorting_id() {
        return sorting_id;
    }

    public void setSorting_id(String sorting_id) {
        this.sorting_id = sorting_id;
    }

    public String getSorting_name() {
        return sorting_name;
    }

    public void setSorting_name(String sorting_name) {
        this.sorting_name = sorting_name;
    }

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
