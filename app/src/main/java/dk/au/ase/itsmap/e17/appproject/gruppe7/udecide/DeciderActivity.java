package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.graphics.Bitmap;
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
    private TextView personNameTV, questionTextTV;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore db;
    int num;
    String questionText;
    Poll currentPoll;
    FirebaseHelper fbService;
    Bitmap image1, image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);

        final Intent data = getIntent();
        final String userName = data.getStringExtra(CONST.USERNAME);
        intitializeUIElements(data);

        firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUi();
            }
        });

        secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUi();
            }
        });

        num = 50;

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        final String currentPolls = "test1";

        pollsDocRef = db.collection("poll").document(currentPolls);
        currentPoll = fbService.getPollData(pollsDocRef);
    }

    private void intitializeUIElements(Intent data) {
        String personNameText = data.getStringExtra(CONST.PERSON_NAME);
        personNameTV = findViewById(R.id.personTV);
        personNameTV.setText(personNameText);

        questionTextTV = findViewById(R.id.questionTV);

        lastQuestionResult = findViewById(R.id.progressBar);
        lastQuestionResult.setProgress(0);
        lastQuestionResult.setMax(100);

        firstImg = findViewById(R.id.firstQuestionImg);
        secondImg = findViewById(R.id.secondQuestionImg);
    }

    private void imagerClickEvent(View v, String userName) {
        Intent imageClickIntent = new Intent();
        imageClickIntent.putExtra(CONST.USERNAME, userName);
        imageClickIntent.putExtra(CONST.VOTE, v.getId());
//        startActivityForResult(imageClickIntent, CONST.REQUEST_NEXT_IMAGE);
    }

    private void updateUi() {
        questionText = currentPoll.getQuestion();
        questionTextTV.setText(questionText);

        image1 = currentPoll.getImage1();
        firstImg.setImageBitmap(image1);

        image2 = currentPoll.getImage2();
        secondImg.setImageBitmap(image2);

        lastQuestionResult.setProgress(num);
    }
}
