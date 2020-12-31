package com.example.parkmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private Button buttonAddressContinue;
    private Button buttonAddressCurrent;
    private Button buttonAddressCancel;
    private EditText editTextAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        buttonAddressContinue = (Button) findViewById(R.id.id_button_address_continue);
        buttonAddressContinue = (Button) findViewById(R.id.id_button_address_current);
        buttonAddressCancel = (Button) findViewById(R.id.id_button_address_cancel);
        editTextAddress = (EditText) findViewById(R.id.id_edittext_address_address);

    }

    //Get latitude and longitude from address user selected
    public LatLng getLatLng(String address){
        Geocoder coder = new Geocoder(this);
        List<Address> strAddress = null;

        try{
            strAddress = coder.getFromLocationName(address, 5);

            if(strAddress == null){
                return new LatLng(0, 0);

            }

        } catch (IOException e) {
            e.printStackTrace();
            return new LatLng(0, 0);

        }

        Address location = strAddress.get(0);

        return new LatLng(location.getLatitude(), location.getLongitude());

    }

    public void addressContinue(View view) {
        //Save start and end date/time
        long sDate = (long)ParkData.getStartDate();
        long sTime = (long)ParkData.getStartTime();

        long eDate = (long)ParkData.getEndDate();
        long eTime = (long)ParkData.getEndTime();

        ParkData.setStartDT(sDate*10000 + sTime);
        ParkData.setEndDT(eDate*10000 + eTime);

        //Convert address string to latlng
        MainActivity mainActivity = MainActivity.getInstance();

        LatLng addrLatLng = getLatLng(editTextAddress.getText().toString());
        mainActivity.setLocation(addrLatLng);                                   //Set view on map to addresses location

        finish();

    }

    public void addressCurrent(View view) {
        //Save start and end date/time
        long sDate = (long)ParkData.getStartDate();
        long sTime = (long)ParkData.getStartTime();

        long eDate = (long)ParkData.getEndDate();
        long eTime = (long)ParkData.getEndTime();

        ParkData.setStartDT(sDate*10000 + sTime);
        ParkData.setEndDT(eDate*10000 + eTime);

        //Save current location

        //Convert latlng to address string
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        @SuppressLint("MissingPermission")
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        //Set view on map to current location
        MainActivity mainActivity = MainActivity.getInstance();

        mainActivity.setLocation(new LatLng(latitude, longitude));

        finish();

    }

    public void cancel(View view) {
        //Don't save any data

        finish();

    }

}
