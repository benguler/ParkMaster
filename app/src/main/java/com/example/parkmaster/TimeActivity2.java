package com.example.parkmaster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeActivity2 extends AppCompatActivity {

    private Button buttonTime2Continue;
    private Button buttonTime2Cancel;
    private TimePicker endTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time2);

        buttonTime2Continue = (Button) findViewById(R.id.id_button_time2_continue);
        buttonTime2Cancel = (Button) findViewById(R.id.id_button_time2_cancel);
        endTimePicker = (TimePicker) findViewById(R.id.id_time_picker2);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void time2Continue(View view) {
        //Save end time

        int endTime = endTimePicker.getHour()*100 + endTimePicker.getMinute();                                      //hhmm

        if((ParkData.getEndDate()*10000 + endTime) > (ParkData.getStartDate()*10000 + ParkData.getStartTime())) {   //if end time (yyyymmddhhmm) is after start time (yyyyddhhmm)
            ParkData.setEndTime(endTime);

            //Get end date
            Intent intent1 = new Intent(this, AddressActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent1);

            finish();

        }else{      //End time is the same as or before start time
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid time", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }

    }

    public void cancel(View view) {
        //Don't save any data
        ParkData.revert();
        finish();

    }
}
