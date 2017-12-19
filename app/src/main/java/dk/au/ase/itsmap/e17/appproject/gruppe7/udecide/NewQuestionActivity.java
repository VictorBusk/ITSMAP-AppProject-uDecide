package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class NewQuestionActivity extends AppCompatActivity {

    private int REQUEST_CAM1 = 301;
    private int REQUEST_CAM2 = 302;
    public TextView tvQuestion, tvPublicOrFreinds, tvDecitionNotify;
    public SeekBar sbNotify;
    public ImageView ivFirstPic, ivSecondPic;
    public Button btnSaveDec;

    public String notifiy = "0", questionText;
    public Bitmap photo1, photo2;
    public boolean freindsOrPublic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        InitCompoenents();

        //https://stackoverflow.com/questions/15326290/get-android-seekbar-value-and-display-it-on-screen
        sbNotify.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                notifiy = String.valueOf(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            });
        tvDecitionNotify.setText(notifiy);
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
        saveQuestion();
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

    private void InitCompoenents() {
        tvQuestion = (TextView) findViewById(R.id.TVQuestion);
        tvPublicOrFreinds = (TextView) findViewById(R.id.TVForP);
        tvDecitionNotify = (TextView) findViewById(R.id.TVNotificationDec);
        sbNotify = (SeekBar) findViewById(R.id.SBNotify);
        ivFirstPic = (ImageView) findViewById(R.id.IWDecitionOne);
        ivSecondPic = (ImageView) findViewById(R.id.IWDecition2);
        btnSaveDec = (Button) findViewById(R.id.BTNSaveDec);
    }

    public void saveQuestion(){

    }
}
