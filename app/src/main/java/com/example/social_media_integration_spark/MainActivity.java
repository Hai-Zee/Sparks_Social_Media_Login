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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextInputLayout emailSignIn, passwordSignIn;
    TextView createAccountButton1;
    Button signInButton;
    LoginButton facebookSignInButton;
    SignInButton googleSignInButton;
    FirebaseAuth myAuth;
    //    FirebaseAuth.AuthStateListener authStateListener;
    AccessTokenTracker accessTokenTracker;
    FirebaseUser user;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;
    ProgressBar progressBar;

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
        FacebookSdk.sdkInitialize(getApplicationContext());

        createRequestFacebook();
        createRequestGoogle();
        signInFirebase();

        createAccountButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Create?", "onClick: createButton is clicked");
                startActivity(new Intent(MainActivity.this, CreateAccount.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.INVISIBLE);
        } else {
//                facebook code
            callbackManager.onActivityResult(requestCode, resultCode, data);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        myAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    user = myAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                // ...
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        myAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        myAuth.removeAuthStateListener(authStateListener);
    }

    public void access(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        myAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = myAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createRequestFacebook() {

        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                progressBar.setVisibility(View.VISIBLE);

                access(loginResult.getAccessToken());

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancel() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(FacebookException error) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    myAuth.signOut();
                }
            }
        };
    }

    public void createRequestGoogle() {
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG1", "OnClick");
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("483724107113-uovdon0ovedte45kcosf9qckn4q4elln.apps.googleusercontent.com")
                        .requestEmail()
                        .requestId()
                        .requestProfile()
                        .build();
                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    private void signInFirebase() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                String email = emailSignIn.getEditText().getText().toString();
                String password = passwordSignIn.getEditText().getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    emailSignIn.setError(null);
                    passwordSignIn.setError(null);
                    myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        emailSignIn.setError("Required");
                        passwordSignIn.setError(null);
                    } else if (!TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                        emailSignIn.setError(null);
                        passwordSignIn.setError("Required");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void bindings() {
        progressBar = findViewById(R.id.progress_bar_main);
        progressBar.setVisibility(View.GONE);
        emailSignIn = findViewById(R.id.emailTextInputLayoutID);
        passwordSignIn = findViewById(R.id.passwordTextInputLayoutID);
        signInButton = findViewById(R.id.signButtonID);
        facebookSignInButton = findViewById(R.id.facebook_SignIn_buttonID);
        googleSignInButton = findViewById(R.id.googleSignInButtonID);
        createAccountButton1 = findViewById(R.id.createAccountTxtVwID);
    }

    public void updateUI(FirebaseUser user) {
        Log.d("TAG7", "updateUI");
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            if (user.getDisplayName() != null) {
                intent.putExtra("name", user.getDisplayName());
            }
            if (user.getDisplayName() == null) {
                Toast.makeText(this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
            if (user.getEmail() != null) {
                intent.putExtra("email", user.getEmail());
            }
            if (user.getEmail() == null) {
                Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
            }
            if (user.getPhotoUrl() != null) {
                String photoURL = user.getPhotoUrl().toString();
                photoURL = photoURL + "?type=large";
                intent.putExtra("image", photoURL);
            }
            if (user.getPhotoUrl() == null) {
                Toast.makeText(this, "Facebk pe Foto ni lgaya tune", Toast.LENGTH_SHORT).show();
            }
            intent.putExtra("isClicked", true);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MainActivity.this, "You are not logged in bhai..", Toast.LENGTH_SHORT).show();
        }
    }
}