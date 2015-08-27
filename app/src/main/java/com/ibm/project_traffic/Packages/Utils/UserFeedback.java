package com.ibm.project_traffic.Packages.Utils;

import android.widget.TextView;

import java.util.Date;

/**
 * Created by larryasante on 7/23/15.
 */
public class UserFeedback {
    public long timeStamp;
    public  String username;
    public String locationDetails;
    public  String postDetails;

    //Constructor
    public UserFeedback(){

    }

    //Getters
    public long getTimeStamp(){
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return this.username;
    }

    //Setters
    public void setUsername(String username){
        this.username = username;
    }

    public String getLocationDetails() {
        return this.locationDetails;
    }

    public void setLocationDetails(String locationDetails){
        this.locationDetails = locationDetails;
    }

    public String getPostDetails(){
        return this.postDetails;
    }

    public void setPostDetails(String postDetails) {
        this.postDetails = postDetails;
    }

}
