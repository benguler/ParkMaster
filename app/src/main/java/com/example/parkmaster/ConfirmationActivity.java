package com.example.parkmaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkmaster.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class ConfirmationActivity extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 7171;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    private TextView textAddress;
    private TextView textTime;
    private TextView textPrice;

    private Button buttonContinue;
    private Button buttonReturn;

    private String amount = "";

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        //START PAYPAL SERVICE
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        textAddress = findViewById(R.id.id_text_confirmation_address);
        textTime = findViewById(R.id.id_text_confirmation_time);
        textPrice = findViewById(R.id.id_text_confirmation_price);

        buttonContinue = findViewById(R.id.id_button_confirmation_continue);
        buttonReturn = findViewById(R.id.id_button_confirmation_return);

        textAddress.setText(ParkData.getAddress());

        String sTime = String.valueOf(ParkData.getStartDT() + 1000000);     //+1000000 to convert month to display for (e.g. 10 -> 11 for november)

        String eTime = String.valueOf(ParkData.getEndDT() + 1000000);

        String time = "";

        for (int i = 0; i < 12; i++) {
            time += sTime.charAt(i);

            if (i == 3) {           //yyyyy
                time += "/";

            } else if (i == 5) {    //mm
                time += "/";

            } else if (i == 7) {    //hh
                time += " ";

            } else if (i == 9) {    //mm
                time += ":";

            }

        }

        time += " - ";

        for (int i = 0; i < 12; i++) {
            time += eTime.charAt(i);

            if (i == 3) {
                time += "/";

            } else if (i == 5) {
                time += "/";

            } else if (i == 7) {
                time += " ";

            } else if (i == 9) {
                time += ":";

            }

        }

        textTime.setText(time);

        textPrice.setText("Price: $" + String.valueOf(ParkData.getPrice()));

    }

    public void pay(View view) {
        //Will be handle in the payemnt activity
        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.reserveSpot();

        mainActivity.disableConfirmCancel();

        amount = String.valueOf(ParkData.getPrice());
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "Amount Payment", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);


        finish();

    }

    public void done(View view) {
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null)
                {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails ", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }

}
