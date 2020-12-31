package com.example.parkmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class DateActivity1 extends AppCompatActivity {

    private Button buttonDate1Continue;
    private Button buttonDate1Cancel;
    private DatePicker startDatePicker;

    private int curDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_date1);

        buttonDate1Continue = (Button) findViewById(R.id.id_button_date1_continue);
        buttonDate1Cancel = (Button) findViewById(R.id.id_button_date1_cancel);
        startDatePicker = (DatePicker) findViewById(R.id.id_date1_picker);

        Date curTime = Calendar.getInstance().getTime();

        Long curDadte = (long)curTime.getMonth() + (long)(curTime.getYear()+1900)*100;    //Get current date/time

        ParkData.setCurDate(curDate);

    }

    public void date1Continue(View view) {
        int startDate = startDatePicker.getYear()*10000 + startDatePicker.getMonth()*100 + startDatePicker.getDayOfMonth();     //yyyymmdd

        if(startDate >= curDate) {      //If start date is the same or after the current date
            //Save start date
            ParkData.setStartDate(startDate);

            //Find start time
            Intent intent1 = new Intent(this, TimeActivity1.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent1);

            finish();

        }else{      //Start date is before current date
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid date", Toast.LENGTH_SHORT);
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
