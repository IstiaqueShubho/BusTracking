package com.example.istiaque.bustracking;

/**
 * Created by Istiaque on 7/15/2019.
 */

public class Driverinfo {
    private double Latitude;
    private double Longitude;
    private String key;
    private String Busname;

    public Driverinfo(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getBusname() {
        return Busname;
    }

    public void setBusname(String busname) {
        Busname = busname;
    }
}
