package com.example.social_media_integration_spark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextInputLayout emailSignIn, passwordSignIn;
    Button signInButton;
    LoginButton facebookSignInButton;
    SignInButton googleSignInButton;
    FirebaseAuth myAuth;
    FirebaseUser user;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        To hide the the Status Bar --
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindings();
        myAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        facebookSignInButton.setReadPermissions("email", "public_profile");

        createRequestFacebook();
        createRequestGoogle();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chla2", "Chal gya");
                signIn();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("chla5", "Chal gya");

        if (resultCode == RESULT_OK){
            Log.d("chla6", "Chal gya");
//            Google code
                // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d("chla1", "Chal gya");
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        // ...
                    }
                }
            else{
//                facebook code
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d("chla3", "Chal gya");
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            myAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                user = myAuth.getCurrentUser();
                                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                finish();
                            }
                                else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
                            }
//    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
//        @Override
//        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//
//        }
//    };

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser user = myAuth.getCurrentUser();
//        if (user!=null){
//            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//            intent.putExtra("profile pic", user.getPhotoUrl());
//            intent.putExtra("name", user.getDisplayName());
//            intent.putExtra("email", user.getEmail());
//            startActivity(intent);
//            finish();
////        }
//        else {
//            Toast.makeText(MainActivity.this, "User is null", Toast.LENGTH_SHORT).show();
//        }
    }

        public void access (AccessToken accessToken){
            AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
            myAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = myAuth.getCurrentUser();
//                  ------>>>      UPDATE UI HERE  (PENDING)
                         Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                         intent.putExtra("profile pic", user.getPhotoUrl());
                         intent.putExtra("name", user.getDisplayName());
                         intent.putExtra("email", user.getEmail());
                         startActivity(intent);
                         finish();
                    }
                    else{
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

       public void createRequestFacebook(){
           facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
               @Override
               public void onSuccess(LoginResult loginResult) {
                   access(loginResult.getAccessToken());
               }

               @Override
               public void onCancel() {

               }

               @Override
               public void onError(FacebookException error) {

               }
           });
       }

       public void createRequestGoogle(){
           // Configure Google Sign In
           GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                   .requestIdToken(getString(R.string.default_web_client_id))
                   .requestEmail()
                   .build();
           // Build a GoogleSignInClient with the options specified by gso.
           mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

//    Google sign in method
    private void signIn() {
        Log.d("chla4", "Chal gya");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void bindings(){
        emailSignIn = findViewById(R.id.emailTextInputLayoutID);
        passwordSignIn = findViewById(R.id.passwordTextInputLayoutID);
        signInButton = findViewById(R.id.signButtonID);
        facebookSignInButton = findViewById(R.id.facebook_SignIn_buttonID);
        googleSignInButton = findViewById(R.id.googleSignInButtonID);
    }
}