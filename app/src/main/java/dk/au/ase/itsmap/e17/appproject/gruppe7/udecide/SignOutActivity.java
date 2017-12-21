package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;

public class SignOutActivity extends AppCompatActivity {

    private static final String TAG = "SignOutActivity";
    private String[] permissions = new String[] {"read_custom_friendlists", "public_profile", "user_friends", "email"};
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.logout_button);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.i(TAG, "SignOutActivity:onAuthStateChanged");
                    Toast.makeText(SignOutActivity.this, "Authentication State Changed.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignOutActivity.this, SignInActivity.class));
                }
            }
        };

        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "facebook:onSuccess:" + loginResult);
                Toast.makeText(SignOutActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Log.i(TAG, "facebook:onCancel");
                Toast.makeText(SignOutActivity.this, "Authentication canceled.", Toast.LENGTH_SHORT).show();

                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "facebook:onError", error);
                Toast.makeText(SignOutActivity.this, "Authentication error.", Toast.LENGTH_SHORT).show();

                // ...
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
