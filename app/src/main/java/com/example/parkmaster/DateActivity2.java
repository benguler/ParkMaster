package com.example.parkmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

public class DateActivity2 extends AppCompatActivity {
    private Button buttonDate2Continue;
    private Button buttonDate2Cancel;
    private DatePicker endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date2);

        buttonDate2Continue = (Button) findViewById(R.id.id_button_date2_continue);
        buttonDate2Cancel = (Button) findViewById(R.id.id_button_date2_cancel);
        endDatePicker = (DatePicker) findViewById(R.id.id_date2_picker);

    }

    public void date2Continue(View view) {
        int endDate = endDatePicker.getYear()*10000 + endDatePicker.getMonth()*100 + endDatePicker.getDayOfMonth();     //yyyymmdd

        if(endDate >= ParkData.getStartDate()) {    //If end date is the as or after start date
            //Save end date
            ParkData.setEndDate(endDate);

            //Get end time
            Intent intent1 = new Intent(this, TimeActivity2.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent1);

            finish();

        }else{      //End date is before start date
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
