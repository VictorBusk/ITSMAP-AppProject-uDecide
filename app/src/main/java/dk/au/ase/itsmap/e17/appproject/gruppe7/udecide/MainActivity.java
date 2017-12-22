package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

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

import java.util.HashSet;
import java.util.Set;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_FRIENDS_IDS;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_NAME;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_PHOTO_URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private NavigationView navigationView;

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Toast.makeText(MainActivity.this, "You NEED a active internet connection to use this app!", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateUserData();
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            BlankFragment fragment = new BlankFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).commit();
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        Class fragmentClass;

        switch(item.getItemId()) {
            case R.id.nav_decide:
                fragmentClass = DeciderFragment.class;
                break;
            case R.id.nav_myQuestions:
                fragmentClass = MyQuestionsFragment.class;
                break;
            case R.id.nav_newQuestion:
                fragmentClass = NewQuestionFragment.class;
                break;
            case R.id.nav_logout:
                fragmentClass = BlankFragment.class;
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                break;
            default:
                fragmentClass = DeciderFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContent, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateUserData() {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        GraphRequest meRequest = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, "meRequest:JSONObject" + object);
                        Log.i(TAG, "meRequest:GraphResponse" + response);
                        try {
                            editor.putString(FACEBOOK_ID, object.getString("id")).apply();
                            editor.putString(FACEBOOK_NAME, object.getString("name")).apply();
                            editor.putString(FACEBOOK_PHOTO_URL, object.getJSONObject("picture").getJSONObject("data").getString("url")).apply();
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
                        Log.i(TAG, "newMyFriendsRequest:GraphResponse" + response);
                        Set<String> set = new HashSet<String>();
                        for (int i = 0; i < objects.length(); i++) {
                            try {
                                set.add(objects.getJSONObject(i).getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        editor.putStringSet(FACEBOOK_FRIENDS_IDS, set).apply();
                    }
                });

        GraphRequestBatch batch = new GraphRequestBatch(meRequest, myFriendsRequest);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch batch) {
                Log.i(TAG, "newMyFriendsRequest:GraphRequestBatch" + batch);
                updateNavHeader();
            }
        });
        batch.executeAsync();
    }

    private void updateNavHeader() {
        ImageView ivProfilePhotoNav = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_navHeader);
        TextView tvTitleNav = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_title_navHeader);
        TextView tvSubTitleNav = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_subtitle_navHeader);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        Picasso.with(MainActivity.this).load(sharedPref.getString(FACEBOOK_PHOTO_URL, null)).fit().into(ivProfilePhotoNav);
        tvTitleNav.setText(sharedPref.getString(FACEBOOK_NAME, null));
        tvSubTitleNav.setText(sharedPref.getString(FACEBOOK_ID, null));

        Log.i(TAG, "FacebookId: " + sharedPref.getString(FACEBOOK_ID, null));
        Log.i(TAG, "FacebookName: " + sharedPref.getString(FACEBOOK_NAME, null));
        Log.i(TAG, "FacebookPhotoUrl: " + sharedPref.getString(FACEBOOK_PHOTO_URL, null));
        Set<String> stringSet = sharedPref.getStringSet(FACEBOOK_FRIENDS_IDS, null);
        for (String facebookFriendId : stringSet) {
            Log.i(TAG, "FacebookFriendId: "+ facebookFriendId);
        }
    }
}
