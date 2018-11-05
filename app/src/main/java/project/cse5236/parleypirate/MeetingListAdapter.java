package project.cse5236.parleypirate;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MeetingListAdapter extends ArrayAdapter<DocumentSnapshot> {
    private Context mContext;
    private List<DocumentSnapshot> mMeetings = new ArrayList<>();

    public MeetingListAdapter(@NonNull Context context, List<DocumentSnapshot> list) {
        super(context, 0 , list);
        mContext = context;
        mMeetings = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.meeting_list_item,parent,false);

        DocumentSnapshot currentMeeting = mMeetings.get(position);

        TextView name = listItem.findViewById(R.id.meeting_list_item_textview);
        String text = currentMeeting.get("title")+"\n"+currentMeeting.get("starttime").toString()+" to "+currentMeeting.get("endtime").toString();
        name.setText(text);

        return listItem;
    }
}
