package com.example.pixelsort;

import java.util.List;

public class Album {

    private List<Image> image;
    private String album_id;
    private String album_name;
    private String thumbnail;
    private Boolean isSelected = false;

    public Album() {}

    public Album(String album_id, String album_name, String thumbnail, List<Image> image) {
        this.album_id = album_id;
        this.album_name = album_name;
        this.thumbnail = thumbnail;
        this.image = image;
    }
    public List<Image> getImage() {
        return image;
    }
    public void setImage(List<Image> image) {
        this.image = image;
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
    public Boolean getSelected() {
        return isSelected;
    }
    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
