package com.example.parkmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//Share data between activities
public class ParkData {
    private static double price;

    private static double rate;

    private static LatLng ltLng;

    private static String address;

    private static int startDate;

    private static int startTime;

    private static int endDate;

    private static int endTime;

    private static int curDate;

    private static String oldAddress;

    private static int oldStartDate = 0;

    private static int oldStartTime = 0;

    private static int oldEndDate = 0;

    private static int oldEndTime = 0;

    private static long startDT;

    private static long endDT;

    private static String qrSeed;

    private static String userID;

    private static boolean startUp = true;

    public static void saveOld(){
        oldAddress = address;
        oldStartDate = startDate;
        oldStartTime = startTime;
        oldEndDate = endDate;
        oldEndTime = endTime;

    }

    public static void revert(){
        address = oldAddress;
        startDate = oldStartDate;
        startTime = oldStartTime;
        endDate = oldEndDate;
        endTime = oldEndTime;

    }

    //Convert latlng to address string

    public static String latLngToAddr(Context context, double lat, double lng){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Geocoder geocoder;
        List<Address> address = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            address = geocoder.getFromLocation(lat, lng, 1);

        } catch (IOException e) {
            e.printStackTrace();

        }

        return address.get(0).getAddressLine(0);

    }

    public static double getPrice() {
        return price;
    }

    public static void setPrice(double price) {
        ParkData.price = price;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        ParkData.address = address;
    }

    public static int getStartDate() {
        return startDate;
    }

    public static void setStartDate(int startDate) {
        ParkData.startDate = startDate;
    }

    public static int getStartTime() {
        return startTime;
    }

    public static void setStartTime(int startTime) {
        ParkData.startTime = startTime;
    }

    public static int getEndDate() {
        return endDate;
    }

    public static void setEndDate(int endDate) {
        ParkData.endDate = endDate;
    }

    public static int getEndTime() {
        return endTime;
    }

    public static void setEndTime(int endTime) {
        ParkData.endTime = endTime;
    }

    public static int getCurDate() {
        return curDate;
    }

    public static void setCurDate(int curDate) {
        ParkData.curDate = curDate;
    }

    public static LatLng getLtLng() {
        return ltLng;
    }

    public static void setLtLng(LatLng ltLng) {
        ParkData.ltLng = ltLng;
    }

    public static long getStartDT() {
        return startDT;
    }

    public static void setStartDT(long startDT) {
        ParkData.startDT = startDT;
    }

    public static long getEndDT() {
        return endDT;
    }

    public static void setEndDT(long endDT) {
        ParkData.endDT = endDT;
    }

    public static double getRate() {
        return rate;
    }

    public static void setRate(double rate) {
        ParkData.rate = rate;
    }

    public static String getQrSeed() {
        return qrSeed;
    }

    public static void setQrSeed(String qrSeed) {
        ParkData.qrSeed = qrSeed;
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        ParkData.userID = userID;
    }

    public static boolean isStartUp() {
        return startUp;
    }

    public static void setStartUp(boolean startUp) {
        ParkData.startUp = startUp;
    }
}