package com.example.social_media_integration_spark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;

public class SecondActivity extends AppCompatActivity {

    ImageView userImage;
    TextView user_Name, userEmailID;
    FirebaseAuth myAuth;
    String mUserID;
    Button signOutButton;
    DatabaseReference retrieveRef;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        myAuth = FirebaseAuth.getInstance();
        userImage = findViewById(R.id.imageViewID);
        user_Name = findViewById(R.id.nameTextViewID);
        userEmailID = findViewById(R.id.emailTextViewID);
        progressBar = findViewById(R.id.progress_bar_second);
        signOutButton = findViewById(R.id.signOutButtonID);
        progressBar.setVisibility(View.GONE);
        mUserID = myAuth.getCurrentUser().getUid();
        retrieveRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserID);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAuth.signOut();
                startActivity(new Intent(SecondActivity.this, MainActivity.class));
                finish();
            }
        });

        boolean check = false;
        Intent intent = getIntent();
        check = intent.getBooleanExtra("isClicked", false);

        if (check) {

            progressBar.setVisibility(View.VISIBLE);
            userEmailID.setText(intent.getStringExtra("email"));
            user_Name.setText(intent.getStringExtra("name"));
            Glide.with(SecondActivity.this).load(intent.getStringExtra("image")).centerCrop().into(userImage);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            retrieveData();
        }
    }

    private void retrieveData() {

        progressBar.setVisibility(View.VISIBLE);

        retrieveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Data_Module data_module = null;
                for (DataSnapshot myData : snapshot.getChildren()) {

                    data_module = myData.getValue(Data_Module.class);
                }
                userEmailID.setText(data_module.getCreateAccountEmail());
                user_Name.setText(data_module.getUserName());
                Glide.with(SecondActivity.this).load(data_module.getImageURI()).centerCrop().into(userImage);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SecondActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAuth.signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d("chai", "onOptionsItemSelected: " + "hello");
        if (item.getItemId() == R.id.signOutButtonID){
            myAuth.signOut();
            startActivity(new Intent(SecondActivity.this, MainActivity.class));
            finish();
        }
        return true;
    }
}