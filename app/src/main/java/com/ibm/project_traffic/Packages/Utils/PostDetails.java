package com.ibm.project_traffic.Packages.Utils;

import java.io.Serializable;

/**
 * Created by larryasante on 8/3/15.
 */
public class PostDetails implements Serializable {

    //Fields
    String username, from, to , comment, reporting_address, timeStamp;
    double latitude, longitude;
    int id, congestionResponse, totalResponse,  congestion_rating;
    float congestionLevel;
    double est_time;
    double est_density;
    int percentage;


    //Constructor
    public PostDetails(){

    }

    //Getters
    public int getCongestionResponse() {
        return congestionResponse;
    }

    public int getTotalResponse() {
        return totalResponse;
    }

    public String getUsername() {
        return this.username;
    }


    public int getId(){
        return id;
    }

    public String getTo(){
        return this.to;
    }

    public String getComment(){
        return this.comment;
    }

    public String getTimeStamp(){
        return this.timeStamp;
    }

    public String getFrom(){
        return this.from;
    }

    public int getCongestionRating(){
        return this.congestion_rating;
    }

    public String getReportingAddress(){
        return this.reporting_address;
    }

    public double getReportingLocLongitude(){
        return this.longitude;
    }

    public double getPercentage() {
        return percentage;
    }


    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTo(String to){
        this.to = to;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public void setFrom(String from){
        this.from = from;
    }

    public void setCongestionRating(int congestionRating){
        this.congestion_rating = congestionRating;
    }

    public void setReportingAddress(String reportingAddress){
        this.reporting_address = reportingAddress;
    }

    public void setReportingLocLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getReportingLocLatitude(){
        return this.latitude;
    }

    public void setReportingLocLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void setCongestionLevel(float congestion) {
        this.congestionLevel = congestion;
    }

    public void setEstDensity(double est_density) {
        this.est_density = est_density;
    }

    public void setEst_time(double est_time) {
        this.est_time = est_time;
    }

    public void setCongestionResponse(int congestionResponse) {
        this.congestionResponse = congestionResponse;
    }

    public void setTotalResponse(int totalResponse) {
        this.totalResponse = totalResponse;
    }
}
