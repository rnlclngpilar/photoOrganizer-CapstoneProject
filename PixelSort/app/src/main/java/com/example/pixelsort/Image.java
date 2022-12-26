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
    private String day;
    private String month;
    private String year;
    private String archiveId;
    private Boolean isSelected = false;
    private Boolean highQuality = false;
    private String imageId;
    private long timeTagInteger;

    //images
    public Image(String ImageURL, ArrayList<String> keyWords, String Day, String Month, String Year, long TimeTagInteger, Boolean HighQuality) {
        imageURL = ImageURL;
        keywords = keyWords;
        day = Day;
        month = Month;
        year = Year;
        timeTagInteger = TimeTagInteger;
        highQuality = HighQuality;
    }

    public Image() {

    }

    //Images
    public String getImageURL() { return imageURL; }
    public void setImageURL (String ImageURL) { imageURL = ImageURL; }

    public ArrayList<String> getKeywords() { return keywords; }
    public void setKeywords (ArrayList<String> keyWords) { keywords = keyWords; }

    public String getDay() { return day; }
    public void setDay (String Day) { day = Day; }

    public String getMonth() { return month; }
    public void setMonth (String Month) { month = Month; }

    public String getYear() { return year; }
    public void setYear (String Year) { year = Year; }

    public long getTimeTagInteger() { return timeTagInteger; }
    public void setTimeTagInteger (long TimeTagInteger) { timeTagInteger = TimeTagInteger; }

    public String getKey() { return key; }
    public void setKey (String Key) { key = Key; }
    
    public String getImageId() { return imageId; }
    public void setImageId (String imageID) { imageId = imageID; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey (String StorageKey) { storageKey = StorageKey; }

    public Boolean getSelected() { return isSelected; }
    public void setSelected (Boolean select) { isSelected = select; }

    public String getArchiveId() { return archiveId; }
    public void setArchiveId (String archiveID) { archiveId = archiveID; }

    public Boolean getHighQuality() { return highQuality; }
    public void setHighQuality (Boolean HighQuality) { highQuality = HighQuality; }

}
