package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

//dette kode er inspirerert fra vores weather app der blev afleverert i handin 2.

public class QuestionAdaptor extends BaseAdapter{

    private Context context;
    private String question;
    private int progressStatus;
    private ArrayList<Poll> polls;
    private Poll poll;

    public QuestionAdaptor(Context context, ArrayList<Poll> pollList){
        this.context = context;
        this.polls = pollList;
    }

    @Override
    public int getCount() {
        if(polls != null)
            return polls.size();
        else
            return 0;
    }

    @Override
    public View getView(int position, View customView, ViewGroup viewGroup) {

        if (customView == null){
            LayoutInflater Inflator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            customView = Inflator.inflate(R.layout.question_list_item, null);

            poll = polls.get(position);

            if(poll != null){
                TextView txtQuestion = customView.findViewById(R.id.tvQuestion);
                txtQuestion.setText(poll.getQuestion());
                ProgressBar progressBar = customView.findViewById(R.id.QLWprogressBar);

                double votePercentage = (poll.getImage1Votes()/(poll.getImage1Votes() + poll.getImage2Votes()))*100;
                progressBar.setProgress((int) votePercentage);
            }
            return customView;
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        if(polls != null)
            return polls.get(i);
        else
            return null;
    }

}

