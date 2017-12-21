package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class NewPollActivity extends AppCompatActivity {

    private int REQUEST_CAM1 = 301;
    private int REQUEST_CAM2 = 302;
    private TextView tvPublicOrFreinds, tvDecitionNotify;
    private SeekBar sbNotify;
    private ImageView ivFirstPic, ivSecondPic;
    private Button btnSaveDec, btnCancel;
    private int notifyCount = 0;
    private Bitmap photo1, photo2;
    private RadioButton rbPublic, rbFriends;
    private EditText etQuestion;
    private boolean freindsOrPublic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);
        InitComponents();

        //https://stackoverflow.com/questions/15326290/get-android-seekbar-value-and-display-it-on-screen
        sbNotify.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                notifyCount = progress;
                tvDecitionNotify.setText( " " + String.valueOf(notifyCount));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            });
        tvPublicOrFreinds.setText(R.string.newPollNotify);
        rbFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freindsOrPublic = true;
            }
        });
        rbPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freindsOrPublic = false;
            }
        });

        //https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
        ivFirstPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent inCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (inCamera.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(inCamera, REQUEST_CAM1);
                    }
                }

        });

        ivSecondPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent inCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (inCamera.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(inCamera, REQUEST_CAM2);
                    }
                }
        });

        btnSaveDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save data to firebase.
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_CAM1 && resultCode == RESULT_OK){
            photo1 = (Bitmap) data.getExtras().get("data");
            ivFirstPic.setImageBitmap(photo1);
        }
        if( requestCode == REQUEST_CAM2 && resultCode == RESULT_OK){
            photo2 = (Bitmap) data.getExtras().get("data");
            ivSecondPic.setImageBitmap(photo2);
        }
    }

    private void InitComponents() {
        etQuestion = findViewById(R.id.etQuestion);
        tvPublicOrFreinds = findViewById(R.id.TVForP);
        tvDecitionNotify = findViewById(R.id.TVNotificationDec);
        sbNotify = findViewById(R.id.SBNotify);
        ivFirstPic = findViewById(R.id.IWDecitionOne);
        ivSecondPic = findViewById(R.id.IWDecition2);
        btnSaveDec = findViewById(R.id.BTNSaveDec);
        btnCancel = findViewById(R.id.BTNCancel);
        rbFriends = findViewById(R.id.RBFreinds);
        rbPublic = findViewById(R.id.RBPublic);
    }
}