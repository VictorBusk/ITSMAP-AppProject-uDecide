package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.STORAGE_IMAGES_PATH;

public class DeciderActivity extends AppCompatActivity {

    private DocumentReference pollsDocRef;
    private ImageView firstImg, secondImg;
    private TextView personNameTV, questionTextTV, myProgressTextTv;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore db;
    int num;
    String questionText, imageId1, imageId2;
    Bitmap bmp;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);
        final Intent data = getIntent();
        intitializeUIElements(data);

        firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPollData();
            }
        });

        secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPollData();
            }
        });

        num = 50;

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        final String currentPolls = "ZFxMhPLpNYfgcQUVHjcL"; //Jeppe jeg skal have poll fra user id filtering! :D
        CollectionReference pollsCollection = db.collection(CONST.DB_POLLS_COLLECTION);
        pollsDocRef = pollsCollection.document(currentPolls);
        getPollData();
    }

    private void intitializeUIElements(Intent data) {
        String personNameText = data.getStringExtra(CONST.PERSON_NAME);
        personNameTV = findViewById(R.id.personTV);
        personNameTV.setText(personNameText);

        myProgressTextTv = findViewById(R.id.myTextProgress);
        questionTextTV = findViewById(R.id.questionTV);

        lastQuestionResult = findViewById(R.id.progressBar);
        lastQuestionResult.setMax(100);
        lastQuestionResult.setProgress(50);

        //to set text
        myProgressTextTv.setText("50");


        firstImg = findViewById(R.id.firstQuestionImg);
        secondImg = findViewById(R.id.secondQuestionImg);
    }

    private void updateUI(Poll currentPoll) {
        questionText = currentPoll.getQuestion();
        questionTextTV.setText(questionText);

        imageId1 = currentPoll.getImage1ID();
        imageId2 = currentPoll.getImage2ID();

        lastQuestionResult.setProgress(num);
    }

    public void getPollData() {
        pollsDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            Log.d(String.valueOf(this), "DocumentSnapshot data: " + documentSnapshot.getData());
                            Poll poll = documentSnapshot.toObject(Poll.class);
                            updateUI(poll);
                            getImage(imageId1, firstImg);
                            getImage(imageId2, secondImg);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(String.valueOf(this), "Unable to extract poll from Firebase", e);
                    }
                });
    }

    public void getImage(String imageId, final ImageView imageView) {
        storageRef.child(STORAGE_IMAGES_PATH + imageId).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println(getResources().getString(R.string.GenericImageError) + exception.toString());
            }
        });
    }
}
