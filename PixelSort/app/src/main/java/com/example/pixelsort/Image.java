package com.example.pixelsort;

import java.util.ArrayList;

public class Image {
    private String imageURL;
    private String key;
    private String storageKey;
    private ArrayList<String> keywords = new ArrayList<>();
    private ArrayList<String> confidence = new ArrayList<>();
    private String day;
    private String month;
    private String year;
    private String archiveId;
    private Boolean isSelected = false;
    private Boolean highQuality = false;
    private double qualityScore;
    private String imageId;
    private String dateId;

    private String yearId;
    private int counter;
    private long timeTagInteger;
    private long reverseTimeTagInteger;

    //images
    public Image() {

    }

    public Image(String ImageURL, ArrayList<String> keyWords, ArrayList<String> Confidence, String Day, String Month, String Year, long TimeTagInteger, long ReverseTimeTagInteger, Boolean HighQuality) {
        imageURL = ImageURL;
        keywords = keyWords;
        confidence = Confidence;
        day = Day;
        month = Month;
        year = Year;
        timeTagInteger = TimeTagInteger;
        reverseTimeTagInteger = ReverseTimeTagInteger;
        highQuality = HighQuality;
    }

    public Image(String image_url, ArrayList<String> keywordsArray, ArrayList<String> confidenceArray, String Day, String Month, String Year, long TimeTagInteger, long ReverseTimeTagInteger, Boolean HighQuality, double QualityScore) {
        imageURL = image_url;
        keywords = keywordsArray;
        confidence = confidenceArray;
        day = Day;
        month = Month;
        year = Year;
        timeTagInteger = TimeTagInteger;
        reverseTimeTagInteger = ReverseTimeTagInteger;
        highQuality = HighQuality;
        qualityScore = QualityScore;
    }

    public Image(String image_url, ArrayList<String> keywordsArray, ArrayList<String> confidenceArray, String Day, String Month, String Year, long TimeTagInteger, long ReverseTimeTagInteger) {
        imageURL = image_url;
        keywords = keywordsArray;
        confidence = confidenceArray;
        day = Day;
        month = Month;
        year = Year;
        timeTagInteger = TimeTagInteger;
        reverseTimeTagInteger = ReverseTimeTagInteger;
    }

    //Images
    public String getImageURL() { return imageURL; }
    public void setImageURL (String ImageURL) { imageURL = ImageURL; }

    public ArrayList<String> getKeywords() { return keywords; }
    public void setKeywords (ArrayList<String> keyWords) { keywords = keyWords; }

    public ArrayList<String> getConfidence() { return confidence; }
    public void setConfidence (ArrayList<String> Confidence) { confidence = Confidence; }

    public String getDay() { return day; }
    public void setDay (String Day) { day = Day; }

    public String getMonth() { return month; }
    public void setMonth (String Month) { month = Month; }

    public String getYear() { return year; }
    public void setYear (String Year) { year = Year; }

    public long getTimeTagInteger() { return timeTagInteger; }
    public void setTimeTagInteger (long TimeTagInteger) { timeTagInteger = TimeTagInteger; }

    public long getReverseTimeTagInteger() { return reverseTimeTagInteger; }
    public void setReverseTimeTagInteger (long ReverseTimeTagInteger) { reverseTimeTagInteger = ReverseTimeTagInteger; }

    public String getKey() { return key; }
    public void setKey (String Key) { key = Key; }
    
    public String getImageId() { return imageId; }
    public void setImageId (String imageID) { imageId = imageID; }

    public String getDateId() { return dateId; }
    public void setDateId (String dateID) { dateId = dateID; }

    public String getYearId() { return yearId; }
    public void setYearId (String yearID) { yearId = yearID; }

    public int getCounter() { return counter; }
    public void setCounter (int Counter) { counter = Counter; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey (String StorageKey) { storageKey = StorageKey; }

    public Boolean getSelected() { return isSelected; }
    public void setSelected (Boolean select) { isSelected = select; }

    public String getArchiveId() { return archiveId; }
    public void setArchiveId (String archiveID) { archiveId = archiveID; }

    public Boolean getHighQuality() { return highQuality; }
    public void setHighQuality (Boolean HighQuality) { highQuality = HighQuality; }

    public double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(double qualityScore) {
        this.qualityScore = qualityScore;
    }

}
