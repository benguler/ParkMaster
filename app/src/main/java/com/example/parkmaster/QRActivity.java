package com.example.parkmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRActivity extends AppCompatActivity {

    private ImageView qrCode;
    private Button buttonReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        qrCode = (ImageView) findViewById(R.id.id_qr_code);

        //Create bitmap of QR code using generated seed
        Bitmap bitmap = encodeAsBM(ParkData.getQrSeed());
        //Set that bitmap to image view
        qrCode.setImageBitmap(bitmap);

        buttonReturn = (Button) findViewById(R.id.id_button_qr_return);

    }

    //Create bitmap of QR code
    Bitmap encodeAsBM(String seed){
        BitMatrix bM = null;

        try {
            bM = new MultiFormatWriter().encode(seed, BarcodeFormat.QR_CODE, 200, 200, null);

        } catch (WriterException e) {
            e.printStackTrace();

        }

        int width = bM.getWidth();
        int height = bM.getHeight();

        int[] pixels = new int[width*height];

        for(int y = 0; y < height; y++){
            int offset = y * width;

            for (int x = 0; x < width; x++){
                pixels[offset + x] = bM.get(x, y) ? Color.BLACK : Color.WHITE;

            }

        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 200, 0, 0, width, height);

        return bitmap;

    }

    public void done(View view) {
        finish();

    }

}
