package com.example.social_media_integration_spark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth myAuth;
    FirebaseUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        if(myUser!=null){
            startActivity(new Intent(SplashScreen.this, SecondActivity.class));
        }
        else{
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        }
        finish();
    }
}