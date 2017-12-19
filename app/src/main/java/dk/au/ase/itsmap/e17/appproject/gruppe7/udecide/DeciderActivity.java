package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.Models.Polls;

public class DeciderActivity extends AppCompatActivity {

    private DocumentReference pollsDocRef;
    private ImageView personImg, questionFirstImg, questionSecondImg;
    private TextView personNameTV, questionTextTV;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore mFirestore;
    int num;
    String someText = "Which one..?";
    private String mPollsKey;
    public static final String POLLS_KEY = "polls_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get post key from intent
        mPollsKey = getIntent().getStringExtra(POLLS_KEY);
        if (mPollsKey == null) {
            throw new IllegalArgumentException("Must pass POLLS_KEY");
        }

        // Inspired by: https://firebase.google.com/docs/database/android/start/ and https://www.youtube.com/watch?v=kDZYIhNkQoM
        // Attach a listener to read the data at our posts reference

        pollsDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Polls polls = documentSnapshot.toObject(Polls.class);
            }
        });

        final Intent data = getIntent();
        final String userName = data.getStringExtra(CONST.USERNAME);

        String personNameText = data.getStringExtra(CONST.PERSON_NAME);
        personNameTV = findViewById(R.id.personTV);
        personNameTV.setText(personNameText);

        String questionText = data.getStringExtra(CONST.QUESTION_TEXT);
        questionTextTV = findViewById(R.id.questionTV);
        questionTextTV.setText(questionText);

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
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                // [START_EXCLUDE]
                mAuthorView.setText(post.author);
                mTitleView.setText(post.title);
                mBodyView.setText(post.body);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
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
