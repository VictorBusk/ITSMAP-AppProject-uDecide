package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers.FirebaseHelper;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

public class DeciderActivity extends AppCompatActivity {

    private DocumentReference pollsDocRef;
    private ImageView firstImg, secondImg;
    private TextView personNameTV, questionTextTV, myProgressTextTv;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore db;
    int num;
    String questionText, imageId1, imageId2;
    Poll currentPoll;
    FirebaseHelper firebaseHelper;
    Bitmap image1, image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);
        final Intent data = getIntent();
        intitializeUIElements(data);

        firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        num = 50;

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        final String currentPolls = "test1";

        pollsDocRef = db.collection("poll").document(currentPolls);
        currentPoll = firebaseHelper.getPollData(pollsDocRef);
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

    private void updateUI() {
        questionText = currentPoll.getQuestion();
        questionTextTV.setText(questionText);

        image1 = firebaseHelper.getImage(imageId1);
        firstImg.setImageBitmap(image1);

        image2 = firebaseHelper.getImage(imageId2);
        secondImg.setImageBitmap(image2);

        lastQuestionResult.setProgress(num);
    }
}
