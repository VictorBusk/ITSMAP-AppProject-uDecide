package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helper.FirebaseHelper;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_POLLS_COLLECTION;

public class NewQuestionFragment extends Fragment {
    private TextView tvDecisionNotify;
    private RadioButton rbPublic, rbFriends;
    private Button btnSaveDec;
    private boolean publicOrFriends;
    private Bitmap photo1, photo2;
    private int notifyNumber = 0;
    private EditText etQuestion;
    private SeekBar sbNotify;
    private View view;
    private String NotifyString, question;
    private Uri imageUri1, imageUri2;
    private final Fragment frag = this;
    private ImageView ivFirstPic, ivSecondPic, ivFirstStorage,
            ivSecondStorage, ivFirstCamera, ivSecondCamera;
    FirebaseHelper firebaseHelper;

    public NewQuestionFragment() {}

    private void InitComponents() {
        etQuestion = view.findViewById(R.id.etQuestion);
        tvDecisionNotify = view.findViewById(R.id.TVNotificationDec);
        sbNotify = view.findViewById(R.id.SBNotify);
        ivFirstPic = view.findViewById(R.id.IWDecitionOne);
        ivFirstPic.setVisibility(View.INVISIBLE);
        ivSecondPic = view.findViewById(R.id.IWDecitionTwo);
        ivSecondPic.setVisibility(View.INVISIBLE);
        btnSaveDec = view.findViewById(R.id.BTNSaveDec);
        rbFriends = view.findViewById(R.id.RBFreinds);
        rbPublic = view.findViewById(R.id.RBPublic);
        ivFirstStorage = view.findViewById(R.id.IWGallery1);
        ivSecondStorage = view.findViewById(R.id.IWGallery2);
        ivFirstCamera = view.findViewById(R.id.IWCamera1);
        ivSecondCamera = view.findViewById(R.id.IWCamera2);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("QuestionText", question);
        outState.putInt("NotifyNumber", notifyNumber);
        outState.putParcelable("PictureOne",photo1);
        outState.putParcelable("PictureTwo", photo2);
       /* outState.putInt("PicVisibility", picVisibility);
        outState.putInt("CameraVisibility", cameraVisibility);
        outState.putInt("StorageVisibility", storageVisibility); */
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            /*ivFirstPic.setVisibility(picVisibility);
            ivSecondPic.setVisibility(picVisibility);
            ivFirstCamera.setVisibility(cameraVisibility);
            ivSecondCamera.setVisibility(cameraVisibility);
            ivFirstStorage.setVisibility(storageVisibility);
            ivSecondStorage.setVisibility(storageVisibility); */
            photo1 = savedInstanceState.getParcelable("PictureOne");
            photo2 = savedInstanceState.getParcelable("PictureTwo");
            question = savedInstanceState.getString("QuestionText");
            notifyNumber = savedInstanceState.getInt("NotifyNumber");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_question, container, false);
        InitComponents();
        firebaseHelper = new FirebaseHelper(getContext());
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(UpdatePollReceiver, new IntentFilter(CONST.POLL_SAVED)); //Listen for a local broadcast with this action


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
        ivFirstStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleStorageClick(CONST.REQUEST_STORAGE1);

            }
        });
        ivSecondStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleStorageClick(CONST.REQUEST_STORAGE2);
            }
        });

        ivFirstCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCameraClick(CONST.REQUEST_CAM1);

            }
        });

        ivSecondCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCameraClick(CONST.REQUEST_CAM2);

            }
        });

        btnSaveDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                question = etQuestion.getText().toString();
                if(question.trim().equals(""))
                {
                    Toast.makeText(getContext().getApplicationContext(),
                            "You need to enter a question", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(photo1 == null || photo2 == null) {
                    Toast.makeText(getContext().getApplicationContext(),
                            "You need to provide two images", Toast.LENGTH_LONG).show();
                    return;
                }
                savePollData();
            }
        });

        return view;
    }

    private void handleStorageClick(int requestCode) {
        Intent inStorage = new Intent(Intent.ACTION_PICK);
        File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String imageDirPath = imageDir.getPath();
        Uri data = Uri.parse(imageDirPath);
        inStorage.setDataAndType(data, "image/*");
        frag.startActivityForResult(inStorage, requestCode);
    }

    private void handleCameraClick(int requestCode) {
        Intent inCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (inCamera.resolveActivity(getActivity().getPackageManager()) != null) {
            frag.startActivityForResult(inCamera, requestCode);
        }
    }

    //https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONST.REQUEST_CAM1) {
            if (resultCode == getActivity().RESULT_OK ) {
                photo1 = (Bitmap) data.getExtras().get("data");
                ivFirstPic.setImageBitmap(photo1);
                ivFirstPic.setVisibility(View.VISIBLE);
                ivFirstStorage.setVisibility(View.INVISIBLE);
                ivFirstCamera.setVisibility(View.INVISIBLE);
            }
        }
        if (requestCode == CONST.REQUEST_CAM2) {
            if (resultCode == getActivity().RESULT_OK) {
                photo2 = (Bitmap) data.getExtras().get("data");
                ivSecondPic.setImageBitmap(photo2);
                ivSecondPic.setVisibility(View.VISIBLE);
                ivSecondStorage.setVisibility(View.INVISIBLE);
                ivSecondCamera.setVisibility(View.INVISIBLE);
            }
        }
        if (requestCode == CONST.REQUEST_STORAGE1) {
            if (resultCode == getActivity().RESULT_OK) {
                imageUri1 = data.getData();
                InputStream inputStream;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri1);
                    photo1 = BitmapFactory.decodeStream(inputStream);
                    ivFirstPic.setVisibility(View.VISIBLE);
                    ivFirstStorage.setVisibility(View.INVISIBLE);
                    ivFirstCamera.setVisibility(View.INVISIBLE);
                    ivFirstPic.setImageBitmap(photo1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CONST.REQUEST_STORAGE2) {
            if (resultCode == getActivity().RESULT_OK) {
                imageUri2 = data.getData();
                InputStream inputStream;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri2);
                    photo2 = BitmapFactory.decodeStream(inputStream);
                    ivSecondPic.setVisibility(View.VISIBLE);
                    ivSecondStorage.setVisibility(View.INVISIBLE);
                    ivSecondCamera.setVisibility(View.INVISIBLE);
                    ivSecondPic.setImageBitmap(photo2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void savePollData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference pollsCollection = db.collection(DB_POLLS_COLLECTION);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getUid();
        String image1ID = firebaseHelper.uploadImageToFirebase(photo1);
        String image2ID = firebaseHelper.uploadImageToFirebase(photo2);

        Poll poll = new Poll(etQuestion.getText().toString(), notifyNumber,
                publicOrFriends, image1ID, image2ID, userID, new Date());
        firebaseHelper.savePollToFirebase(pollsCollection, poll, getContext());
    }

    private void resetUI()
    {
        photo1 = null;
        photo2 = null;
        etQuestion.setText("");
        sbNotify.setProgress(0);
        ivFirstPic.setImageResource(R.drawable.common_full_open_on_phone);
        ivSecondPic.setImageResource(R.drawable.common_full_open_on_phone);
        ivFirstPic.setVisibility(View.INVISIBLE);
        ivSecondPic.setVisibility(View.INVISIBLE);
        ivFirstStorage.setVisibility(View.VISIBLE);
        ivFirstCamera.setVisibility(View.VISIBLE);
        ivSecondStorage.setVisibility(View.VISIBLE);
        ivSecondCamera.setVisibility(View.VISIBLE);
    }

    private BroadcastReceiver UpdatePollReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resetUI();
        }
    };
}