package com.ibm.project_traffic.Packages.Utils;

/**
 * Created by larry on 8/16/15.
 */
public class TrafficStats {

    //Fields
    int id, congestionResponse, totalResponse;
    String from, to;
    float congestionLevel;
    double est_time;
    double est_density;
    double percentage;

    //Constructor
    public TrafficStats(){

    }

    //Getters and Setters


    public void setPercentage(double percentage) {
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

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTotalResponse(int totalResponse) {
        this.totalResponse = totalResponse;
    }

    public void setCongestionResponse(int congestionResponse) {
        this.congestionResponse = congestionResponse;
    }


    //Getters
    public double getEstDensity() {
        return est_density;
    }

    public double getEstTime() {
        return est_time;
    }

    public float getCongestionLevel() {
        return congestionLevel;
    }

    public int getCongestionResponse() {
        return congestionResponse;
    }

    public String getTo() {
        return to;
    }

    public int getId() {
        return id;
    }

    public int getTotalResponse() {
        return totalResponse;
    }

    public String getFrom() {
        return from;
    }

    public double getPercentage() {
        return percentage;
    }
}
