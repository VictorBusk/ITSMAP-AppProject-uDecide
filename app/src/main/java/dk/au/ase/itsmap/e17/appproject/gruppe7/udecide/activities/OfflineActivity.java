package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;

public class OfflineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
    }

    @Override
    public void onBackPressed() {}
}