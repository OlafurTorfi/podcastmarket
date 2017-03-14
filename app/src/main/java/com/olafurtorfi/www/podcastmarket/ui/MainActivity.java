package com.olafurtorfi.www.podcastmarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.olafurtorfi.www.podcastmarket.R;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();


    private static final int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, PodcastsActivity.class);
            startActivity(intent);
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                Log.d(TAG, "onActivityResult: Results were ok and, we started an activity and now we'll finish");
                finish();
                Intent intent = new Intent(this, PodcastsActivity.class);
                startActivity(intent);
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e(TAG, "onActivityResult: null response from signin, was it cancelled?");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e(TAG, "onActivityResult: could not sign in because of NO_NETWORK error");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e(TAG, "onActivityResult: This code is baffled by this unknown error" );
                    return;
                }
            }

            Log.e(TAG, "onActivityResult: The code does not recognise this response, error code:" + response.getErrorCode());
        }
    }
}
