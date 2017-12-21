package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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

public class NewPollActivity extends AppCompatActivity {

    private int REQUEST_CAM1 = 301;
    private int REQUEST_CAM2 = 302;
    public TextView tvQuestion, tvPublicOrFriends, tvDecisionNotify;
    public SeekBar sbNotify;
    public ImageView ivFirstPic, ivSecondPic;
    public Button btnSaveDec;
    private FirebaseFirestore db;

    public int notifyNumber = 0;
    public Bitmap photo1, photo2;
    public boolean publicOrFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);
        InitComponents();

        db = FirebaseFirestore.getInstance();

        //https://stackoverflow.com/questions/15326290/get-android-seekbar-value-and-display-it-on-screen
        sbNotify.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                notifyNumber = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            });
        tvDecisionNotify.setText(notifyNumber);
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
        savePollToFirebase();
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
        tvQuestion = (TextView) findViewById(R.id.TVQuestion);
        tvPublicOrFriends = (TextView) findViewById(R.id.TVForP);
        tvDecisionNotify = (TextView) findViewById(R.id.TVNotificationDec);
        sbNotify = (SeekBar) findViewById(R.id.SBNotify);
        ivFirstPic = (ImageView) findViewById(R.id.IWDecitionOne);
        ivSecondPic = (ImageView) findViewById(R.id.IWDecition2);
        btnSaveDec = (Button) findViewById(R.id.BTNSaveDec);
    }

    public void savePollToFirebase(){
        CollectionReference polls = db.collection("polls");

        String fbUserId = "123";
        String image1id = uploadImage(photo1);
        String image2id = uploadImage(photo1);

        Poll poll = new Poll(tvQuestion.getText().toString(), notifyNumber,
                publicOrFriends, image1id, image2id, fbUserId);

        polls.add(poll);
    }

    public String uploadImage(Bitmap bitmap)
    {
        UUID uuid = UUID.randomUUID();
        final String imageId = uuid.toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + imageId);

        byte[] data = convertBitmap(bitmap);

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
        });

        return imageId;
    }

    private byte[] convertBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return data;
    }
}
