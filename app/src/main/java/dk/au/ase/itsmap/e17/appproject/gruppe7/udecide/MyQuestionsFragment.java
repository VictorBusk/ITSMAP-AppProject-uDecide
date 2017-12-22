package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adaptor.QuestionAdaptor;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyQuestionsFragment extends Fragment {
    //questionAdaptor CustomAdaptor;
    private QuestionAdaptor questionAdaptor;
    private ListView lwQuestions;
    private View view;

    public MyQuestionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_questions, container, false);
        intiComponents();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getUid();
        ArrayList<Poll> polls = getMyPolls(userID);

        questionAdaptor = new QuestionAdaptor(getContext(), polls);
        lwQuestions = view.findViewById(R.id.LVMyQuestions);
        lwQuestions.setAdapter(questionAdaptor);

        //LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(msgReceiver, new IntentFilter(CONST.UPDATE_EVENT));
        //testfunction(getContext());

        return view;
    }

    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String QuestionText = intent.getStringExtra(CONST.QUESTION_TEXT);
            int VoteProgress = intent.getIntExtra(CONST.VOTE,0);
            //CustomAdaptor = new questionAdaptor(context, QuestionText, VoteProgress );
            //CustomAdaptor.notifyDataSetChanged();
            //lwQuestions.setAdapter(CustomAdaptor);

            Bundle extra = intent.getBundleExtra("extra");
            ArrayList<Object> objects = (ArrayList<Object>) extra.getSerializable("objects");

//create our adaptor and attach to ListView
            //questionAdaptor = new questionAdaptor(this, objects);
            //lwQuestions = (ListView)view.findViewById(R.id.LVMyQuestions);
            //lwQuestions.setAdapter(questionAdaptor);
        }
    };

    private void intiComponents() {
        lwQuestions = view.findViewById(R.id.LVMyQuestions);
    }

    private void testfunction(Context context)
    {/*
        CustomAdaptor = new questionAdaptor(getActivity(), "test", 20 );
        CustomAdaptor.notifyDataSetChanged();
        lwQuestions.setAdapter(CustomAdaptor);*/
    }

    private ArrayList<Poll> getMyPolls(String userID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference polls = db.collection("polls");

        final ArrayList<Poll> pollList = new ArrayList<Poll>();

        polls.whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                pollList.add(document.toObject(Poll.class));
                            }
                        } else {
                            Log.d(String.valueOf(this), "Error getting documents: ", task.getException());
                        }
                    }
                });

        return pollList;
    }
}
