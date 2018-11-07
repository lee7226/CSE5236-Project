package project.cse5236.parleypirate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewMeetingsActivity extends AppCompatActivity {

    private static final String TAG = "ViewMeetingsActivity";
    private static final String USERS = "/users/";

    private ListView mListView;

    private List<DocumentSnapshot> meetings;

    private MeetingListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_listview);

        addMeetingsToListView();
    }

    private void addMeetingsToListView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference meetingsRef = db.collection("meetings");
        DocumentReference userRef = db.document(USERS+UserDatabaseId.getDbId());
        final Query query = meetingsRef.whereArrayContains("members", userRef);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot qs = task.getResult();
                if(qs!=null && qs.size()>0) {
                    Log.d(TAG, "found " + qs.size() + " meetings");
                    meetings = new ArrayList<>();
                    meetings.addAll(qs.getDocuments());
                    mAdapter = new MeetingListAdapter(this, meetings);
                    mListView = findViewById(R.id.listview);
                    mListView.setAdapter(mAdapter);
                    mListView.setClickable(true);
                    mListView.setOnItemClickListener((parent, view, position, id) -> {
                        Object o = mListView.getItemAtPosition(position);
                        DocumentSnapshot ds = (DocumentSnapshot) o;
                        Intent meetingOptionsIntent = new Intent(ViewMeetingsActivity.this,MeetingOptionsActivity.class);
                        meetingOptionsIntent.putExtra(getString(R.string.meetingId),ds.getId());
                        startActivity(meetingOptionsIntent);
                    });
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

}
