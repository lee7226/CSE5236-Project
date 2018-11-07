package project.cse5236.parleypirate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

class GroupListAdapter extends ArrayAdapter<DocumentSnapshot> {
    private Context mContext;
    private List<DocumentSnapshot> mGroups;

    public GroupListAdapter(@NonNull Context context, List<DocumentSnapshot> list) {
        super(context, 0 , list);
        mContext = context;
        mGroups = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.meeting_list_item,parent,false);

        DocumentSnapshot currentGroup = mGroups.get(position);

        TextView name = listItem.findViewById(R.id.list_item_textview);
        String text = currentGroup.get("title").toString();
        name.setText(text);

        return listItem;
    }
}
