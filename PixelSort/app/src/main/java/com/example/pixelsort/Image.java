package com.example.pixelsort;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Image {
    private String imageURL;
    private String key;
    private String storageKey;
    private ArrayList<String> keywords = new ArrayList<String>();
    private String timestamp;

    private String albumName;
    private String thumbnail;

    public Image() {}

    //images
    public Image(String ImageURL, ArrayList<String> keyWords, String timeStamp) {
        imageURL = ImageURL;
        keywords = keyWords;
        timestamp = timeStamp;
    }

    //albums
    public Image(String AlbumName, String Thumbnail) {
        albumName = AlbumName;
        thumbnail = Thumbnail;
    }

    //Images
    public String getImageURL() { return imageURL; }
    public void setImageURL (String ImageURL) { imageURL = ImageURL; }

    public ArrayList<String> getKeywords() { return keywords; }
    public void setKeywords (ArrayList<String> keyWords) { keywords = keyWords; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp (String timeStamp) { timestamp = timeStamp; }

    public String getKey() { return key; }
    public void setKey (String Key) { key = Key; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey (String StorageKey) { storageKey = StorageKey; }

    //Albums
    public String getAlbumName() {return albumName;}
    public void setAlbumName(String albumName) {this.albumName = albumName;}

    public String getThumbnail() {return thumbnail;}
    public void setThumbnail(String thumbnail) {this.thumbnail = thumbnail;}
}
