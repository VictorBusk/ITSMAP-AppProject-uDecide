package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeciderActivity extends AppCompatActivity {

    private DocumentReference pollsDocRef;
    private ImageView personImg, questionFirstImg, questionSecondImg;
    private TextView personNameTV, questionTextTV;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore mFirestore;
    int num;
    String questionText;
    String someText = "Which one..?";
    private String mPollsKey;
    public static final String POLLS_KEY = "polls_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);

        final Intent data = getIntent();
        final String userName = data.getStringExtra(CONST.USERNAME);

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
        questionFirstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagerClickEvent(v,userName);
                updateProgressBar();
                updateQuestionText();
            }
        });

        questionSecondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagerClickEvent(v,userName);
                updateProgressBar();
                updateQuestionText();
            }
        });

        updateQuestionText();
        num = 50;

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get polls key from intent
        mPollsKey = getIntent().getStringExtra(POLLS_KEY);
        if (mPollsKey == null) {
            throw new IllegalArgumentException("Must pass POLLS_KEY");
        }

        // Inspired by: https://firebase.google.com/docs/database/android/start/ and https://www.youtube.com/watch?v=kDZYIhNkQoM
        // Attach a listener to read the data at our posts reference
        pollsDocRef.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    questionText = doc.get("question").toString();
                    questionTextTV.setText(questionText);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


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
