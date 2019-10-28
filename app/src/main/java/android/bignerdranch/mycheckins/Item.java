package android.bignerdranch.mycheckins;

import android.location.Location;

import java.util.Date;
import java.util.UUID;

public class Item {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mPlace;
    private String mFriend;
    private String mDetails;

    private double mLatitude;
    private double mLongitude;

    /* Constructors */
    public Item() {
        this(UUID.randomUUID());
    }

    public Item(UUID id) {
        mId = id;
        mDate = new Date();
    }

    /* Methods */
    public UUID getId() {
        return mId;
    }

    /**
     * Get the date when the item was completed
     * @return
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * Set the date when the item was completed
     * @param date
     */
    public void setDate(Date date) {
        mDate = date;
    }

    /**
     * Get the name of the item
     * @return
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set the title of the item
     * @param title
     */
    public void setTitle(String title) {
        mTitle = title;
    }


    /**
     * Get the friend you were with
     * @return
     */
    public String getFriend() {
        return mFriend;
    }

    /**
     * Set the friend you were with
     * @param friend
     */
    public void setFriend(String friend) {
        mFriend = friend;
    }

    /**
     * Get the photo filename
     * @return
     */
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }


    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }
}
