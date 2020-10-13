package com.example.social_media_integration_spark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URL;

public class SecondActivity extends AppCompatActivity {

    ImageView userImage;
    TextView userName, userEmailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        userImage = findViewById(R.id.imageViewID);
        userName = findViewById(R.id.nameTextViewID);
        userEmailID = findViewById(R.id.emailTextViewID);

        Intent intent = new Intent();
        userName.setText(intent.getStringExtra("name"));
        userEmailID.setText(intent.getStringExtra("email"));
        Uri image = intent.getData();

        Glide.with(SecondActivity.this).load(image).centerCrop().into(userImage);
    }
}