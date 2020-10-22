package com.example.social_media_integration_spark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class CreateAccount extends AppCompatActivity {
    TextInputLayout createEmail, createPassword, enterName;
    Button createAccountButton;
    FirebaseAuth myAuth;
    DatabaseReference myReference;
    FirebaseStorage myStorage;
    StorageReference myStorageReference;
    TextView createAccountSignIn;
    ImageView selectImage;
    private static final int RC = 1;
    Uri imageURI;
    String mUserID;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setUpFirebase();
        bindViews();

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), RC);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String createAccountEmail = createEmail.getEditText().getText().toString().trim();
                final String createAccountPass = createPassword.getEditText().getText().toString().trim();
                final String userName = enterName.getEditText().getText().toString().trim();

                if(!TextUtils.isEmpty(createAccountEmail) && !TextUtils.isEmpty(createAccountPass) && !TextUtils.isEmpty(userName)){
                    createEmail.setError(null);
                    createPassword.setError(null);
                    enterName.setError(null);
                    progressBar.setVisibility(View.VISIBLE);

                    myAuth.createUserWithEmailAndPassword(createAccountEmail, createAccountPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                pushData(createAccountEmail, userName);
//                                startActivity(new Intent(CreateAccount.this, SecondActivity.class));
                                Toast.makeText(CreateAccount.this, "Account Created", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else{
                                Toast.makeText(CreateAccount.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    if(TextUtils.isEmpty(createAccountEmail) && !TextUtils.isEmpty(createAccountPass) && !TextUtils.isEmpty(userName)){
                        createEmail.setError("Required");
                        createPassword.setError(null);
                        enterName.setError(null);
                    }
                    else if(!TextUtils.isEmpty(createAccountEmail) && TextUtils.isEmpty(createAccountPass) && !TextUtils.isEmpty(userName)){
                        createEmail.setError(null);
                        createPassword.setError("Required");
                        enterName.setError(null);
                    }
                    else if (!TextUtils.isEmpty(createAccountEmail) && !TextUtils.isEmpty(createAccountPass) && TextUtils.isEmpty(userName)){
                        createEmail.setError(null);
                        createPassword.setError(null);
                        enterName.setError("Required");
                    }
                    else {
                        createEmail.setError("Required");
                        createPassword.setError("Required");
                        enterName.setError("Required");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });


        createAccountSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this, MainActivity.class));
            }
        });
       }

    private void setUpFirebase() {
        myAuth = FirebaseAuth.getInstance();
        myStorage = FirebaseStorage.getInstance();
        myReference = FirebaseDatabase.getInstance().getReference().child("Users");
        myStorageReference = myStorage.getReference();
    }

    private void pushData(final String createAccountEmail, final String userName) {

        StorageReference filepath = myStorageReference.child("sparks").child(imageURI.getLastPathSegment());
        filepath.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String downloadedUri = uri.toString();
                            mUserID = myAuth.getCurrentUser().getUid();
                            Data_Module data = new Data_Module(createAccountEmail, userName, downloadedUri);
                            Log.d("mogli", "onSuccess: " + mUserID);
                            String randomID = myReference.push().getKey();
                            myReference.child(mUserID).child(randomID).setValue(data);
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC && resultCode == RESULT_OK){
            imageURI = data.getData();
            Glide.with(this).load(imageURI).centerCrop().into(selectImage);
        }
    }

    private void bindViews() {
        progressBar = findViewById(R.id.progress_bar_create);
        progressBar.setVisibility(View.GONE);
        createEmail = findViewById(R.id.createEmailTextInputLayoutID);
        createPassword = findViewById(R.id.createPassTextInputLayoutID);
        enterName = findViewById(R.id.enterNameID2);
        selectImage = findViewById(R.id.imageViewID2);
        createAccountButton = findViewById(R.id.signUpButtonID);
        createAccountSignIn = findViewById(R.id.signInTextViewID2);
    }
}