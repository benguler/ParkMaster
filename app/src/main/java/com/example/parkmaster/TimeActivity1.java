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

public class TimeActivity1 extends AppCompatActivity {

    private Button buttonTime1Continue;
    private Button buttonTime1Cancel;
    private TimePicker startTimePicker;

    private int curTime;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time1);

        buttonTime1Continue = (Button) findViewById(R.id.id_button_time1_continue);
        buttonTime1Cancel = (Button) findViewById(R.id.id_button_time1_cancel);
        startTimePicker = (TimePicker) findViewById(R.id.id_time1_picker);

        //Save current time
        curTime = startTimePicker.getHour()*100 + startTimePicker.getMinute();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void time1Continue(View view) {
        int startTime = startTimePicker.getHour()*100 + startTimePicker.getMinute();                //hhmm

        if(ParkData.getStartDate()*10000 + startTime >= ParkData.getCurDate()*10000 + curTime) {    //if start time (yyyymmddhhmm) is the same as or before end time (yyyymmddhhmm)
            //Save start time
            ParkData.setStartTime(startTime);

            //Get end date
            Intent intent1 = new Intent(this, DateActivity2.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent1);

            finish();

        }else{      //Start time is before current time
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
