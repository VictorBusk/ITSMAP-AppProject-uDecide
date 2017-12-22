package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adaptor.questionAdaptor;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyQuestionsFragment extends Fragment {
    questionAdaptor CustomAdaptor;
    private int rownr;
    private ListView lwQuestions;
    private View view;
    public MyQuestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_questions, container, false);
        intiComponents();
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(msgReceiver, new IntentFilter(CONST.UPDATE_EVENT));
        testfunction(getContext());
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
        }
    };
    private void intiComponents() {
        lwQuestions = view.findViewById(R.id.LVMyQuestions);
    }
    private void testfunction(Context context)
    {
        CustomAdaptor = new questionAdaptor(getActivity(), "test", 20 );
        CustomAdaptor.notifyDataSetChanged();
        lwQuestions.setAdapter(CustomAdaptor);
    }

}
