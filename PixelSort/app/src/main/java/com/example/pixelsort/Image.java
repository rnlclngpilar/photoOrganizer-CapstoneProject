package com.example.pixelsort;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Image {
    private String imageURL;
    private String key;
    private String storageKey;
    private ArrayList<String> keywords = new ArrayList<>();
    private String timestamp;
    private Boolean isSelected = false;
    private Boolean highQuality = false;

    //images
    public Image(String ImageURL, ArrayList<String> keyWords, String timeStamp, Boolean HighQuality) {
        imageURL = ImageURL;
        keywords = keyWords;
        timestamp = timeStamp;
        highQuality = HighQuality;
    }

    public Image() {

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

    public Boolean getSelected() { return isSelected; }
    public void setSelected (Boolean select) { isSelected = select; }

    public Boolean getHighQuality() { return highQuality; }
    public void setHighQuality (Boolean HighQuality) { highQuality = HighQuality; }

}
