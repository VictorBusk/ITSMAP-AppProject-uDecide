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

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

//dette kode er inspirerert fra vores weather app der blev afleverert i handin 2.

public class questionAdaptor extends BaseAdapter{

    private Context context;
    private String question;
    private int progressStatus;

    public questionAdaptor(Context context, String Question, int voteProgress){
    this.context = context;
    this.question = Question;
    this.progressStatus = voteProgress;
    }

    @Override
    public int getCount() {
            return 0;
    }

    @Override
    public View getView(int position, View customView, ViewGroup viewGroup) {
        RecyclerView.ViewHolder holder;
        if (customView == null){
            LayoutInflater Inflator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            customView = Inflator.inflate(R.layout.question_list_item, null);

            if(question != null){
                TextView txtQuestion = customView.findViewById(R.id.tvQuestion);
                txtQuestion.setText(question);
                ProgressBar progressBar = customView.findViewById(R.id.QLWprogressBar);
                progressBar.setProgress(progressStatus);
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
        return null;
    }

}

