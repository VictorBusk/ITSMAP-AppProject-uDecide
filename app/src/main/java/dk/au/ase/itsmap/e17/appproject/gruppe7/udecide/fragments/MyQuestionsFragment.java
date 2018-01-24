package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adapters.MyQuestionsAdapter;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_POLLS_COLLECTION;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_USER_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.SHARED_PREFERENCES;

public class MyQuestionsFragment extends Fragment {

    private MyQuestionsAdapter adapter;
    private ListView listView;
    private List<Poll> polls = new ArrayList<Poll>();
    private String facebookId;

    private SharedPreferences sharedPref;
    private FirebaseFirestore db;
    private CollectionReference pollsRef;

    // Required empty public constructor
    public MyQuestionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_questions, container, false);
        listView = view.findViewById(R.id.myQuestionsList);
        sharedPref = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        facebookId = sharedPref.getString(FACEBOOK_ID, null);
        db = FirebaseFirestore.getInstance();
        pollsRef = db.collection(DB_POLLS_COLLECTION);

        updatePolls();

        return view;
    }

    private void updatePolls() {
        final FragmentActivity activity = getActivity();

        pollsRef.whereEqualTo(DB_USER_ID, facebookId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            polls.add(documentSnapshot.toObject(Poll.class));
                        }

                        if (polls != null) {
                            adapter = new MyQuestionsAdapter(activity, polls);
                            listView.setAdapter(adapter);
                        }
                    }
                });
    }
}