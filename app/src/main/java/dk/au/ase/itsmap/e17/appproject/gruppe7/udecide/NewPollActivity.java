package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static android.app.PendingIntent.getActivity;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.STORAGE_IMAGES_PATH;

public class NewPollActivity extends AppCompatActivity {

    private int REQUEST_CAM1 = 301;
    private int REQUEST_CAM2 = 302;
    private TextView tvPublicOrFriends, tvDecisionNotify;
    private ImageView ivFirstPic, ivSecondPic;
    private RadioButton rbPublic, rbFriends;
    private Button btnSaveDec, btnCancel;
    private boolean publicOrFriends;
    private Bitmap photo1, photo2;
    private int notifyNumber = 0;
    private EditText etQuestion;
    private SeekBar sbNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);
        InitComponents();

        tvDecisionNotify.setText(getResources().getString(R.string.newPollNotify) + " " + String.valueOf(notifyNumber));
        //https://stackoverflow.com/questions/15326290/get-android-seekbar-value-and-display-it-on-screen
        sbNotify.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                notifyNumber = progress;
                tvDecisionNotify.setText(getResources().getString(R.string.newPollNotify) + " " + String.valueOf(notifyNumber));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        rbFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicOrFriends = true;
            }
        });

        rbPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicOrFriends = false;
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
                if(photo1 == null || photo2 == null) {
                    Toast.makeText(getApplicationContext(),
                            "You need to select two images.", Toast.LENGTH_LONG).show();
                    return;
                }

                savePollToFirebase();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void InitComponents() {
        etQuestion = findViewById(R.id.etQuestion);
        tvPublicOrFriends = findViewById(R.id.TVForP);
        tvDecisionNotify = findViewById(R.id.TVNotificationDec);
        sbNotify = findViewById(R.id.SBNotify);
        ivFirstPic = findViewById(R.id.IWDecitionOne);
        ivSecondPic = findViewById(R.id.IWDecition2);
        btnSaveDec = findViewById(R.id.BTNSaveDec);
        btnCancel = findViewById(R.id.BTNCancel);
        rbFriends = findViewById(R.id.RBFreinds);
        rbPublic = findViewById(R.id.RBPublic);
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

    public void savePollToFirebase()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference polls = db.collection("polls");

        String userID = "123";
        String image1ID = uploadImage(photo1);
        String image2ID = uploadImage(photo2);

        Poll poll = new Poll(etQuestion.getText().toString(), notifyNumber,
                publicOrFriends, image1ID, image2ID, userID);

        polls.add(poll);
    }

    public String uploadImage(Bitmap bitmap)
    {
        UUID uuid = UUID.randomUUID();
        final String imageID = uuid.toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(STORAGE_IMAGES_PATH + imageID);

        byte[] data = convertBitmap(bitmap);

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(String.valueOf(this), "Unable to upload image to Firebase", exception);
            }
        });

        return imageID;
    }

    private byte[] convertBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return data;
    }
}