package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    private View view;
    private TextView tvHeadline, tvSubheadline;
    private ImageView ivPhoto;
    private final Fragment frag = this;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_blank, container, false);

        ivPhoto = (ImageView) view.findViewById(R.id.iv_blankFragment);
        tvHeadline = (TextView) view.findViewById(R.id.tv_blankFragment);
        tvSubheadline = (TextView) view.findViewById(R.id.tv_blankFragment2);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        tvHeadline.setText(user.getDisplayName());
        tvSubheadline.setText(user.getProviderData().get(1).getUid());

        Picasso.with(getContext()).load(user.getPhotoUrl()).into(ivPhoto);

        return view;
    }

}
