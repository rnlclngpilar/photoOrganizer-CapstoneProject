package com.example.pixelsort;

public class Album {
    private String albumID;
    private String albumName;
    private String thumbnail;
    private String key;

    public Album() {}

    public Album(String albumID, String albumName, String thumbnail, String key) {
        this.albumID = albumID;
        this.albumName = albumName;
        this.thumbnail = thumbnail;
        this.key = key;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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
