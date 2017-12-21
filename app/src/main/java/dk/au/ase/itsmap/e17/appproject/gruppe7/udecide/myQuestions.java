package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.BroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;

//import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adaptor.questionAdaptor;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import java.util.ArrayList;


public class myQuestions extends AppCompatActivity {
    private static ArrayList<Poll> questionList;
    //questionAdaptor CustomAdaptor;
    private int rownr;
    private ListView lwQuestions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);
        intiComponents();
/*
        //insert data into the poll,
        Poll newPollModel = new Poll();
        questionList.add(rownr, newPollModel);
        CustomAdaptor = new (context, );
        CustomAdaptor. */
    }

    //private BroadcastReceiver();
    private void intiComponents()
    {
        lwQuestions = findViewById(R.id.LWMyQuestions);
    }

}
