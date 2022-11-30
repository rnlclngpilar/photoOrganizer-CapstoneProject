package com.example.pixelsort;

public class Album {
    private String album_id;
    private String album_name;
    private String thumbnail;
    private String key;

    public Album() {}

    public Album(String album_id, String album_name, String thumbnail, String key) {
        this.album_id = album_id;
        this.album_name = album_name;
        this.thumbnail = thumbnail;
        this.key = key;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
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
