package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.STORAGE_IMAGES_PATH;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewQuestionFragment extends Fragment {
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
    private View view;
    private String NotifyString;
    private final Fragment frag = this;

    public NewQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_question, container, false);
        InitComponents();
        NotifyString = getResources().getString(R.string.newPollNotify) + " " + String.valueOf(notifyNumber);
        tvDecisionNotify.setText(NotifyString );

        //https://stackoverflow.com/questions/15326290/get-android-seekbar-value-and-display-it-on-screen
        sbNotify.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                notifyNumber = progress;
                NotifyString = getResources().getString(R.string.newPollNotify) + " " + String.valueOf(notifyNumber);
                tvDecisionNotify.setText(NotifyString );
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
        //https://stackoverflow.com/questions/15408240/take-photo-from-camera-in-fragment
        ivFirstPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (inCamera.resolveActivity(getActivity().getPackageManager()) != null) {
                    frag.startActivityForResult(inCamera, REQUEST_CAM1);
                }
            }
        });

        ivSecondPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (inCamera.resolveActivity(getActivity().getPackageManager()) != null) {
                   frag.startActivityForResult(inCamera, REQUEST_CAM2);
                }
            }
        });

        btnSaveDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(photo1 == null || photo2 == null) {
                    Toast.makeText(getContext().getApplicationContext(),
                            "You need to select two images.", Toast.LENGTH_LONG).show();
                    //return;
                }

               savePollToFirebase();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
            }
        });
        return view;
    }

    private void InitComponents() {
        etQuestion = view.findViewById(R.id.etQuestion);
        tvPublicOrFriends = view.findViewById(R.id.TVForP);
        tvDecisionNotify = view.findViewById(R.id.TVNotificationDec);
        sbNotify = view.findViewById(R.id.SBNotify);
        ivFirstPic = view.findViewById(R.id.IWDecitionOne);
        ivSecondPic = view.findViewById(R.id.IWDecition2);
        btnSaveDec = view.findViewById(R.id.BTNSaveDec);
        btnCancel = view.findViewById(R.id.BTNCancel);
        rbFriends = view.findViewById(R.id.RBFreinds);
        rbPublic = view.findViewById(R.id.RBPublic);

    }
    //https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
    //https://stackoverflow.com/questions/15408240/take-photo-from-camera-in-fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CAM1){
                if (resultCode == getActivity().RESULT_OK ) {
                    photo1 = (Bitmap) data.getExtras().get("data");
                    ivFirstPic.setImageBitmap(photo1);
                }
            }
            if (requestCode == REQUEST_CAM2) {
                if (resultCode == getActivity().RESULT_OK){
                    photo2 = (Bitmap) data.getExtras().get("data");
                    ivSecondPic.setImageBitmap(photo2);
                }
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
