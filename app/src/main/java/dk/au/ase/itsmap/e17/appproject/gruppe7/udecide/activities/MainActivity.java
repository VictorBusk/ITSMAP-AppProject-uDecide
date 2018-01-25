package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.fragments.BlankFragment;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.fragments.DeciderFragment;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.fragments.MyQuestionsFragment;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.fragments.NewQuestionFragment;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers.ConnectivityHelper;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers.FirebaseHelper;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.services.BackgroundService;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_FRIENDS_IDS;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_LAST_UPDATE;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_NAME;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_PHOTO_URL;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.LAST_POLL_TIMESTAMP;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.SHARED_PREFERENCES;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    FirebaseHelper firebaseHelper;
    private NewQuestionFragment newQuestionFragment;
    private MyQuestionsFragment myQuestionsFragment;
    private DeciderFragment deciderFragment;
    private BlankFragment blankFragment;
    private NavigationView navigationView;
    private ConnectivityHelper connectivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityHelper = new ConnectivityHelper(MainActivity.this);

        firebaseHelper = new FirebaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpFragments();

        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();

            NewQuestionFragment savedNewQuestionFragment =
                    (NewQuestionFragment) manager.getFragment(savedInstanceState,
                            NewQuestionFragment.class.getSimpleName());

            if (savedNewQuestionFragment != null)
                newQuestionFragment = savedNewQuestionFragment;
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContent, blankFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (connectivityHelper.isConnected(getApplicationContext()))
            getUser();
        else
            startActivity(new Intent(MainActivity.this, OfflineActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        connectivityHelper.unRegisterReceiver();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            setFragment(blankFragment);
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            stopBackgroundService();
            editor.remove(FACEBOOK_LAST_UPDATE).apply();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            return true;
        } else if (id == R.id.action_reset) {
            setFragment(blankFragment);
            editor.remove(LAST_POLL_TIMESTAMP);
            editor.remove(FACEBOOK_LAST_UPDATE);
            editor.apply();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (item.getItemId()) {
            case R.id.nav_decide:
                fragment = deciderFragment;
                break;
            case R.id.nav_myQuestions:
                fragment = myQuestionsFragment;
                break;
            case R.id.nav_newQuestion:
                fragment = newQuestionFragment;
                break;
            case R.id.nav_logout:
                fragment = blankFragment;
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                stopBackgroundService();
                editor.remove(FACEBOOK_LAST_UPDATE).apply();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                break;
            default:
                fragment = deciderFragment;
        }

        setFragment(fragment);

        return true;
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(fragment.getClass().getName(), 0);

        if (!fragmentPopped) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContent, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void getUser() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        FirebaseUser user = firebaseHelper.extractUser();
        if (user != null) {
            Long nextTimestamp = sharedPref.getLong(FACEBOOK_LAST_UPDATE, 0);
            if (nextTimestamp < new Date().getTime()) {
                updateUserData();
                startBackgroundService();
                editor.putLong(FACEBOOK_LAST_UPDATE, new Date().getTime() + 180000).apply();
            } else {
                Log.d(TAG, "getUser: next update: " + new Date(nextTimestamp));
//                updateNavHeader();
            }
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }

    // ITSMAP L7 Services and Asynch Processing - DemoCode: ServicesDemo
    private void startBackgroundService() {
        Log.i(TAG, "startBackgroundService");
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Intent backgroundServiceIntent = new Intent(MainActivity.this, BackgroundService.class);
        backgroundServiceIntent.putExtra(FACEBOOK_ID, sharedPref.getString(FACEBOOK_ID, null));
        startService(backgroundServiceIntent);
    }

    private void stopBackgroundService() {
        Log.i(TAG, "stopBackgroundService");
        Intent backgroundServiceIntent = new Intent(MainActivity.this, BackgroundService.class);
        stopService(backgroundServiceIntent);
    }

    // https://developers.facebook.com/docs/android/graph
    private void updateUserData() {
        Log.i(TAG, "updateUserData");

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        GraphRequest meRequest = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, "meRequest:JSONObject" + object);
                        try {
                            editor.putString(FACEBOOK_ID, object.getString("id"));
                            editor.putString(FACEBOOK_NAME, object.getString("name"));
                            editor.putString(FACEBOOK_PHOTO_URL, object.getJSONObject("picture").getJSONObject("data").getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle meParameters = new Bundle();
        meParameters.putString("fields", "id,name,picture.type(large)");
        meRequest.setParameters(meParameters);

        GraphRequest myFriendsRequest = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.i(TAG, "newMyFriendsRequest:JSONArray" + objects);
                        Set<String> set = new HashSet<String>();
                        for (int i = 0; i < objects.length(); i++) {
                            try {
                                set.add(objects.getJSONObject(i).getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        editor.putStringSet(FACEBOOK_FRIENDS_IDS, set);
                    }
                });

        GraphRequestBatch batch = new GraphRequestBatch(meRequest, myFriendsRequest);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch batch) {
                Log.i(TAG, "newMyFriendsRequest:GraphRequestBatch" + batch);
                editor.commit();
                updateNavHeader();
            }
        });
        batch.executeAsync();
    }

    // https://stackoverflow.com/questions/42243341/navigation-drawer-header-how-to-put-name-and-profile-pic-image-from-google-sign
    private void updateNavHeader() {
        Log.i(TAG, "updateNavHeader");

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        ImageView ivProfilePhotoNav = navigationView.getHeaderView(0).findViewById(R.id.iv_navHeader);
        TextView tvTitleNav = navigationView.getHeaderView(0).findViewById(R.id.tv_title_navHeader);
        TextView tvSubTitleNav = navigationView.getHeaderView(0).findViewById(R.id.tv_subtitle_navHeader);

        Picasso.with(MainActivity.this).load(sharedPref.getString(FACEBOOK_PHOTO_URL, null)).fit().into(ivProfilePhotoNav);
        tvTitleNav.setText(sharedPref.getString(FACEBOOK_NAME, null));
        tvSubTitleNav.setText(sharedPref.getString(FACEBOOK_ID, null));
    }

    public void setUpFragments() {
        if (newQuestionFragment == null) {
            newQuestionFragment = new NewQuestionFragment();

        if (myQuestionsFragment == null)
            myQuestionsFragment = new MyQuestionsFragment();

        if (deciderFragment == null)
            deciderFragment = new DeciderFragment();

        if (blankFragment == null)
            blankFragment = new BlankFragment();
        }
    }
}
