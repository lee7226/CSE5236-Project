package project.cse5236.parleypirate;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InviteMembersFragment extends Fragment {
    private static final String TAG = "InviteMembersFragment";

    public InviteMembersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invite_members, container, false);
        return v;
    }


}
