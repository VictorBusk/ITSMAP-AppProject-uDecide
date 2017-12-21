package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private String[] permissions = new String[] {"read_custom_friendlists", "public_profile", "user_friends", "email"};

    private FirebaseAuth auth;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        // If using in a fragment
        // loginButton.setFragment(this);
        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "facebook:onSuccess:" + loginResult);
                Toast.makeText(SignInActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "facebook:onCancel");
                Toast.makeText(SignInActivity.this, "Authentication canceled.", Toast.LENGTH_SHORT).show();

                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "facebook:onError", error);
                Toast.makeText(SignInActivity.this, "Authentication error.", Toast.LENGTH_SHORT).show();

                // ...
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i(TAG, "onStart:user:true");
            Toast.makeText(SignInActivity.this, "Welcomen back " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        } else {
            Log.i(TAG, "onStart:user:false");
            Toast.makeText(SignInActivity.this, "You need to login", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.i(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "signInWithCredential:success");
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(SignInActivity.this, "Authentication complete.", Toast.LENGTH_SHORT).show();

                            if (user != null) {
                                Log.i(TAG, "signInWithCredential:success:uid " + user.getUid());
                                Log.i(TAG, "signInWithCredential:success:pid " + user.getProviderId());
                                Log.i(TAG, "signInWithCredential:success:displayName " + user.getDisplayName());
                                Log.i(TAG, "signInWithCredential:success:email " + user.getEmail());
                                Log.i(TAG, "signInWithCredential:success:phone " + user.getPhoneNumber());
                                Log.i(TAG, "signInWithCredential:success:photoUrl " + user.getPhotoUrl());
                                for (UserInfo profile : user.getProviderData()) {
                                    Log.i(TAG, "signInWithCredential:success:profile:uid " + profile.getUid());
                                    Log.i(TAG, "signInWithCredential:success:profile:pid " + profile.getProviderId());
                                    Log.i(TAG, "signInWithCredential:success:profile:displayName " + profile.getDisplayName());
                                    Log.i(TAG, "signInWithCredential:success:profile:email " + profile.getEmail());
                                    Log.i(TAG, "signInWithCredential:success:profile:phone " + profile.getPhoneNumber());
                                    Log.i(TAG, "signInWithCredential:success:profile:photoUrl " + profile.getPhotoUrl());
                                }
                            }

                            startActivity(new Intent(SignInActivity.this, MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
