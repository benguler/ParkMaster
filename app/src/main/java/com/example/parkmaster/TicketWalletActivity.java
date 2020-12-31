package com.example.parkmaster;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class TicketWalletActivity extends AppCompatActivity {

    private ListView ticketList;

    private ArrayList<String> qrSeeds;
    private ArrayList<String> tickets;

    private ArrayAdapter<String> adapter;

    private Button qrButton;
    private Button returnButton;

    private DatabaseHelper db;
    private Cursor dataCursor;

    private boolean ticketAvailable;

    private Date curTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_wallet);

        ticketList = (ListView) findViewById(R.id.id_listview_wallet_ticket);

        qrSeeds = new ArrayList<String>();
        tickets = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, tickets);

        ticketList.setAdapter(adapter);

        qrButton = (Button) findViewById(R.id.id_button_wallet_ticket);
        returnButton = (Button) findViewById(R.id.id_button_wallet_return);

        qrButton.setEnabled(false);
        qrButton.setTextColor(Color.GRAY);

        db = new DatabaseHelper(this);

        dataCursor = db.getDatawName(ParkData.getUserID()); //Get data associated with an id

        ticketAvailable = true;

        curTime = Calendar.getInstance().getTime();

        Long curDT = (long)curTime.getMinutes() + (long)curTime.getHours()*100 + (long)curTime.getDay()*10000 + (long)curTime.getMonth()*1000000 + (long)(curTime.getYear()+1900)*100000000;    //Get current date/time

        String ticket = "";

        while (!dataCursor.isAfterLast()) {

            if(curDT < dataCursor.getLong(4)) { //If current date time is after end date/time for ticket;
                String sDT = String.valueOf(dataCursor.getLong(3) + 1000000);     //+1000000 to convert month to display for (e.g. 10 -> 11 for november)

                String eDT = String.valueOf(dataCursor.getLong(4) + 1000000);

                qrSeeds.add(dataCursor.getString(6));

                ticket = "Address: ";

                ticket += ParkData.latLngToAddr(this, dataCursor.getDouble(2), dataCursor.getDouble(1));

                ticket += "\nTime: ";

                for (int i = 0; i < 12; i++) {
                    ticket += sDT.charAt(i);

                    if (i == 3) {           //yyyyy
                        ticket += "/";

                    } else if (i == 5) {    //mm
                        ticket += "/";

                    } else if (i == 7) {    //hh
                        ticket += " ";

                    } else if (i == 9) {    //mm
                        ticket += ":";

                    }

                }

                ticket += " - ";

                for (int i = 0; i < 12; i++) {
                    ticket += eDT.charAt(i);

                    if (i == 3) {
                        ticket += "/";

                    } else if (i == 5) {
                        ticket += "/";

                    } else if (i == 7) {
                        ticket += " ";

                    } else if (i == 9) {
                        ticket += ":";

                    }

                }


                tickets.add(ticket);
                adapter.notifyDataSetChanged();

            }

            dataCursor.move(1);

        }

        if(ticket == ""){                       //If not tickets were created
            tickets.add("No tickets to show");
            adapter.notifyDataSetChanged();

            ticketAvailable = false;

        }

        ticketList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(ticketAvailable) {
                    ParkData.setQrSeed(qrSeeds.get(position));

                    qrButton.setEnabled(true);
                    qrButton.setTextColor(Color.BLACK);


                }

            }

        });

    }

    //Launch QR ticket activity
    public void showTicket(View view) {
            Intent intent1 = new Intent(this, QRActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent1);

    }

    public void done(View view) {
        finish();

    }

}
