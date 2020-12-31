package com.example.parkmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;

    private TextView textViewUsername;

    private Button buttonSignIn;
    private Button buttonExit;
    private Button buttonCont;
    private Button buttonSignOut;

    private static SignInActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        textViewUsername = (TextView) findViewById(R.id.id_text_siginin_username);

        buttonSignIn = (Button) findViewById(R.id.id_button_signin_signin);
        buttonExit = (Button) findViewById(R.id.id_button_signin_exit);
        buttonCont = (Button) findViewById(R.id.id_button_signin_cont);
        buttonSignOut = (Button) findViewById(R.id.id_button_signin_signout);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId().requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonCont.setEnabled(false);
        buttonCont.setTextColor(Color.GRAY);

        buttonSignOut.setEnabled(false);
        buttonSignOut.setTextColor(Color.GRAY);

        instance = this;

    }

    @Override
    public void onStart(){
        super.onStart();

        if(!ParkData.isStartUp()) {                                     //If this is after application start
            //Sign back in to current account
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);

            buttonCont.setEnabled(true);
            buttonCont.setTextColor(Color.BLACK);
            buttonCont.setText("Return");

            buttonSignOut.setEnabled(true);
            buttonSignOut.setTextColor(Color.BLACK);

            buttonExit.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);  //Get account

            ParkData.setUserID(account.getId());                                        //Save Is associated with that account
            textViewUsername.setText(account.getEmail());                               //Display email associated with that accout


        } catch (ApiException e) {
            // Show error in log
            Log.w("EEEEEEEEEEEEEEEE", "signInResult:failed code=" + e.getStatusCode());

        }

    }


    public void signIn(View view) {
        Intent signInIntent = googleSignInClient.getSignInIntent(); //Get login intent
        startActivityForResult(signInIntent, RC_SIGN_IN);           //Launch signin activity. Will automatically login to account if previously signed in

        buttonCont.setEnabled(true);
        buttonCont.setTextColor(Color.BLACK);

        buttonSignOut.setEnabled(true);
        buttonSignOut.setTextColor(Color.BLACK);

    }

    //Sign out of currently logged in account
    public void signOut(View view) {
        signOutOfAcc();

    }

    public void signOutOfAcc(){
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        buttonCont.setEnabled(false);
                        buttonCont.setTextColor(Color.GRAY);

                        buttonSignOut.setEnabled(false);
                        buttonSignOut.setTextColor(Color.GRAY);

                        textViewUsername.setText("Login to see username");

                    }

                });

    }


    public void done(View view) {
        finish();

        signOutOfAcc();

        System.exit(0);

    }

    //Go to main activity
    public void cont(View view) {
        if (ParkData.getUserID() != null){      //User iD was saved

            if(ParkData.isStartUp()) {
                //Launch main activity
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                this.startActivity(intent1);

                ParkData.setStartUp(false);

            }

            finish();

        }else{
            //Show error toast
            Toast toast = Toast.makeText(getApplicationContext(), "Error with login. Please try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }

        finish();

    }

    public static SignInActivity getInstance(){
        return instance;

    }

}
