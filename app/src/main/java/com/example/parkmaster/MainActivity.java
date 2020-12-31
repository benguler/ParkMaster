package com.example.parkmaster;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private Button buttonTime, buttonConfirm, buttonCancel, buttonQR, buttonExit, buttonAccount;

    private GoogleMap map;

    private static MainActivity instance;

    private DatabaseHelper db;

    private LatLng selectedLatLng;

    //Test Data
    private ArrayList<LatLng> testCoords = new ArrayList<LatLng>();
    private ArrayList<Double> testPrices = new ArrayList<Double>();
    private ArrayList<MarkerOptions> spotMarkers = new ArrayList<MarkerOptions>();

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTime = (Button) findViewById(R.id.id_button_timeloc_main);
        buttonConfirm = (Button) findViewById(R.id.id_button_confirm_main);
        buttonCancel = (Button) findViewById(R.id.id_button_cancel_main);
        buttonQR = (Button) findViewById(R.id.id_button_qr_main);
        buttonExit = (Button) findViewById(R.id.id_button_exit_main);
        buttonAccount = (Button) findViewById(R.id.id_button_account_main);

       disableConfirmCancel();

        instance = this;

        db = new DatabaseHelper(this);

        //Test Data
        testCoords.add(new LatLng(42.6559, -71.3240));
        testPrices.add(0.25);

        testCoords.add(new LatLng(42.6500, -71.3266));
        testPrices.add(0.5);

        testCoords.add(new LatLng(42.6599, -71.3180));
        testPrices.add(0.75);

        //Add data to database, only run once per version. Must be run once per version.
        for(int i = 0; i < testCoords.size(); i++){
            //db.insertData("Space", testCoords.get(i).longitude, testCoords.get(i).latitude, (long)0, (long)0, testPrices.get(i), "");

        }

        //Ask user permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

            return;

        }else{
            //Already have user persmission

        }

        //Add map to fragment
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.id_frag_main_map);    //Define map fragment
        mapFragment.getMapAsync(MainActivity.this);                                                           //Create map on screen

    }

    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;                                                                                                   //Define map object

        //When a marker is clicked
        this.map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){                                               //Put stuff here when marker is clicked on
                    public boolean onMarkerClick(final Marker marker){

                        selectedLatLng = marker.getPosition();      //Save LatLng of selected marker

                        Cursor priceCursor = db.getPrice(selectedLatLng.longitude, selectedLatLng.latitude);


                       enablebleConfirmCancel();

                        //Save price associated with spot
                        ParkData.setRate(priceCursor.getDouble(0));

                        //Show Address and Price
                        marker.showInfoWindow();

                        //Save address of spot
                        ParkData.setAddress(ParkData.latLngToAddr(MainActivity.this, marker.getPosition().latitude, marker.getPosition().longitude));

                        return true;

                    }
                });

        this.map.setMyLocationEnabled(true);

    }

    //Set loaction and draw spaces
    public void setLocation(LatLng latLng){
        //Drawing spaces means a new date and time was selected
       disableConfirmCancel();

        //Move map to slected address
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        //Draw available spaces
        Cursor locCursor = db.getLoca();                //Get unique latitude and longitudes
        LatLng spaceLoc;
        double lat;
        double lng;

        Cursor priceCurser;


        while(!locCursor.isAfterLast()){
            lat = locCursor.getDouble(1);   //Save lat
            lng = locCursor.getDouble(0);   //and lng of space

            locCursor.move(1);                   //Get next space ready

            spaceLoc = new LatLng(lat, lng);

            priceCurser = db.getPrice(lng, lat);

            if(db.CheckAvail(lng, lat, ParkData.getStartDT(), ParkData.getEndDT())) {       //If space available
                MarkerOptions marker = new MarkerOptions().position(spaceLoc).title(ParkData.latLngToAddr(this, lat, lng) + " $" + priceCurser.getDouble(0) + "/min");
                map.addMarker(marker);          //Draw space on map

            }

        }

    }

    //Launch date and time activity
    public void selectTimeLocation(View view) {
        //Save old data
        ParkData.saveOld();

        //Ask for new date and time and location
        Intent intent1 = new Intent(this, DateActivity1.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent1);

    }

    //Confirm selected spot for selected time, launch payment activity
    public void confirm(View view){
        //Save final price
        ParkData.setPrice(calcPrice());

        Intent intent1 = new Intent(this, ConfirmationActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent1);

    }

    //Calculate the price of a reservation giving a rate and length of time
    public Double calcPrice(){
        //Get minutes spot will be reserved

        long startDT = ParkData.getStartDT();

        int startMin = 0, startHour = 0, startDay = 0, startMonth = 0, startYear = 0;

        //Get individual date/time components for start year
        for(int i = 0; i < 12; i ++){
            if(i < 2){
                startMin = (int)(startDT % 10) * (int)Math.pow(10, i) + startMin;

            }else if(i < 4){
                startHour = (int)(startDT % 10) * (int)Math.pow(10, i-2) + startHour;

            }else if(i < 6){
                startDay = (int)(startDT % 10) * (int)Math.pow(10, i-4) + startDay;

            }else if(i < 8){
                startMonth = (int)(startDT % 10) * (int)Math.pow(10, i-6) + startMonth;

            }else{
                startYear = (int)(startDT % 10) * (int)Math.pow(10, i-8) + startYear;

            }

            startDT = startDT / 10;

        }

        long endDT = ParkData.getEndDT();

        long endMin = 0, endHour = 0, endDay = 0, endMonth = 0, endYear = 0;

        //Get individual date/time components for end year
        for(int i = 0; i < 12; i ++){
            if(i < 2){
                endMin = (int)(endDT % 10) * (int)Math.pow(10, i) + endMin;

            }else if(i < 4){
                endHour = (int)(endDT % 10) * (int)Math.pow(10, i-2) + endHour;

            }else if(i < 6){
                endDay = (int)(endDT % 10) * (int)Math.pow(10, i-4) + endDay;

            }else if(i < 8){
                endMonth = (int)(endDT % 10) * (int)Math.pow(10, i-6) + endMonth;

            }else{
                endYear = (int)(endDT % 10) * (int)Math.pow(10, i-8) + endYear;

            }

            endDT = endDT / 10;

        }

        boolean startLeapYear = false;
        boolean endLeapYear = false;

        //Check if the starting year is a leap year
        if(startYear % 4 == 0){
            if(startYear % 100 == 0){
                if(startYear % 400 == 0){
                    startLeapYear = true;

                }

            }else{
                startLeapYear = true;

            }

        }


        //Check if the ending year is a leap year
        if(endYear % 4 == 0){
            if(endYear % 100 == 0){
                if(endYear % 400 == 0){
                    endLeapYear = true;

                }

            }else{
                endLeapYear = true;

            }

        }

        long yearToMin = (endYear - startYear) * 365 * 24 * 60;     //Convert years to minutes

        long monToMin = 0;                                          //Convert months to minutes

        switch((int)endMonth){
            case 0:      //Jan
                monToMin = 0;
                break;
            case 1:     //Feb
                monToMin = 31;
                break;
            case 2:     //Mar
                monToMin = 31 + 28;
                break;
            case 3:     //Apr
                monToMin = 31 + 28 + 31;
                break;
            case 4:     //May
                monToMin = 31 + 28 + 31 + 30;
                break;
            case 5:     //Jun
                monToMin = 31 + 28 + 31 + 30 + 31;
                break;
            case 6:     //Jul
                monToMin = 31 + 28 + 31 + 30 + 31 + 30;
                break;
            case 7:     //Aug
                monToMin = 31 + 28 + 31 + 30 + 31 + 30 + 31;
                break;
            case 8:     //Sep
                monToMin = 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31;
                break;
            case 9:     //Oct
                monToMin = 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30;
                break;
            case 10:    //Nov
                monToMin = 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31;
                break;
            case 11:    //Dec
                monToMin = 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30;
                break;
            default:
                monToMin = 0;
                break;

        }

        switch((int)startMonth){
            case 0:     //Jan
                monToMin -= 0;
                break;
            case 1:     //Feb
                monToMin -= 31;
                break;
            case 2:     //Mar
                monToMin -= 31 + 28;
                break;
            case 3:     //Apr
                monToMin -= 31 + 28 + 31;
                break;
            case 4:     //May
                monToMin -= 31 + 28 + 31 + 30;
                break;
            case 5:     //Jun
                monToMin -= 31 + 28 + 31 + 30 + 31;
                break;
            case 6:     //Jul
                monToMin -= 31 + 28 + 31 + 30 + 31 + 30;
                break;
            case 7:     //Aug
                monToMin -= 31 + 28 + 31 + 30 + 31 + 30 + 31;
                break;
            case 8:     //Sep
                monToMin -= 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31;
                break;
            case 9:     //Oct
                monToMin -= 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30;
                break;
            case 10:    //Nov
                monToMin -= 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31;
                break;
            case 11:    //Dec
                monToMin -= 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30;
                break;

        }

        if(startLeapYear && startMonth > 0){                            //If starting year is a leap year and starting month is after january
            if((startMonth == 1 && startDay == 29) || startMonth > 1) { //If starting day is after feb 28
                monToMin -= 1;

            }

        }

        if(endLeapYear && endMonth > 0){                                //If starting day year is a leap year and starting month is after january
            if((endMonth == 1 && endDay == 29) || endMonth > 1) {       //If ending day is after feb 28
                monToMin += 1;

            }

        }

        monToMin = monToMin * 24 * 60;

        long dayToMin = (endDay - startDay) * 24 * 60;                  //Convert days to minutes

        long hourToMin = (endHour - startHour) * 60;                    //Convert hours to minutes

        long minToMin = (endMin - startMin);

        //Calcite final price given minutes and rate
        return (yearToMin + monToMin + dayToMin + hourToMin + minToMin) * ParkData.getRate();

    }

    //Add reserve spot to database
    public void reserveSpot(){

       //Generate random string as seed for QR code
       byte[] array = new byte [15];
       new Random().nextBytes(array);
       String genSeed = new String(array, Charset.forName("UTF-8"));

       //Add reserved space to database;
        Cursor priceCursor = db.getPrice(selectedLatLng.longitude, selectedLatLng.latitude);
       db.insertData(ParkData.getUserID(), selectedLatLng.longitude, selectedLatLng.latitude, ParkData.getStartDT(), ParkData.getEndDT(), priceCursor.getDouble(0), genSeed);  //Add reserved spot to database

    }

    //Cancel selection
    public void cancel(View view){
        //Cancel selected spot
        disableConfirmCancel();

        selectedLatLng = null;

    }

    //Launch ticket wallet activity
    public void selectTicket(View view){
        //Get list of QR tickets

        Intent intent1 = new Intent(this, TicketWalletActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent1);

    }

    //Launch account activity
    public void account(View view){
        Intent intent1 = new Intent(this, SignInActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent1);

    }

    //Exit aplication
    public void exit(View view){

        //Sign out of account
        SignInActivity signInActivity = SignInActivity.getInstance();
        signInActivity.signOutOfAcc();

        finish();
        System.exit(0);

    }

    //Return instance of main activity
    public static MainActivity getInstance(){
        return instance;

    }

    public void enablebleConfirmCancel(){
        buttonConfirm.setEnabled(true);
        buttonCancel.setEnabled(true);

        buttonConfirm.setTextColor(Color.BLACK);
        buttonCancel.setTextColor(Color.BLACK);

    }

    public void disableConfirmCancel(){
        buttonConfirm.setEnabled(false);
        buttonCancel.setEnabled(false);

        buttonConfirm.setTextColor(Color.GRAY);
        buttonCancel.setTextColor(Color.GRAY);

    }

}
