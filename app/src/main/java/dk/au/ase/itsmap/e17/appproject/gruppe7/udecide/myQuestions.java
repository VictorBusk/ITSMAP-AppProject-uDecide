package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adaptor.QuestionAdaptor;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import java.util.ArrayList;


public class myQuestions extends AppCompatActivity {
    QuestionAdaptor questionAdaptor;
    private int rownr;
    private ListView lwQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);
        intiComponents();
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, new IntentFilter(CONST.UPDATE_EVENT));
    }

    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String QuestionText = intent.getStringExtra(CONST.QUESTION_TEXT);
            //skal måske omdøbe CONTST.VOTE.
            int VoteProgress = intent.getIntExtra(CONST.VOTE,0);
            //CustomAdaptor = new questionAdaptor(context, QuestionText, VoteProgress );
            //lwQuestions.setAdapter(CustomAdaptor,);
        }
    };

    private void intiComponents() {
        lwQuestions = findViewById(R.id.LWMyQuestions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }
}
