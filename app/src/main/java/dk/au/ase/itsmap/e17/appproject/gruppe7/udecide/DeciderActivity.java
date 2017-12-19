package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers.FirebaseHelper;

public class DeciderActivity extends AppCompatActivity {

    private DocumentReference pollsDocRef;
    private ImageView personImg, questionFirstImg, questionSecondImg;
    private TextView personNameTV, questionTextTV;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore db;
    int num;
    String someText = "Which one..?";
    FirebaseHelper fbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);

        final Intent data = getIntent();
        final String userName = data.getStringExtra(CONST.USERNAME);
        intitializeUIElements(data);

        questionFirstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagerClickEvent(v, userName);
                updateProgressBar();
                updateQuestionText();
            }
        });

        questionSecondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagerClickEvent(v, userName);
                updateProgressBar();
                updateQuestionText();
            }
        });

        updateQuestionText();
        num = 50;

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        final String currentPolls = "test1";

        pollsDocRef = db.collection("poll").document(currentPolls);
        //fbService.getPollData(pollsDocRef);
    }

    private void intitializeUIElements(Intent data) {
        String personNameText = data.getStringExtra(CONST.PERSON_NAME);
        personNameTV = findViewById(R.id.personTV);
        personNameTV.setText(personNameText);

        questionTextTV = findViewById(R.id.questionTV);

        lastQuestionResult = findViewById(R.id.progressBar);
        lastQuestionResult.setProgress(0);
        lastQuestionResult.setMax(100);

        questionFirstImg = findViewById(R.id.firstQuestionImg);
        questionSecondImg = findViewById(R.id.secondQuestionImg);

        personImg = findViewById(R.id.personImg);
    }

    private void imagerClickEvent(View v, String userName) {
        Intent imageClickIntent = new Intent();
        imageClickIntent.putExtra(CONST.USERNAME, userName);
        imageClickIntent.putExtra(CONST.VOTE, v.getId());
//        startActivityForResult(imageClickIntent, CONST.REQUEST_NEXT_IMAGE);
    }

    private void updateProgressBar() {
        lastQuestionResult.setProgress(num);
    }

    private void updateQuestionText() {
        questionTextTV.setText(someText);
    }

    private void loadUserDetails() {

    }
}
