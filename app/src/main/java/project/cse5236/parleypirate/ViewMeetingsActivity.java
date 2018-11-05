package project.cse5236.parleypirate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        setContentView(R.layout.activity_view_meetings);

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
                    mListView = findViewById(R.id.meetings_listview);
                    mListView.setAdapter(mAdapter);
                    mListView.setClickable(true);
                    mListView.setOnItemClickListener((parent, view, position, id) -> {
                        Object o = mListView.getItemAtPosition(position);
                        DocumentSnapshot ds = (DocumentSnapshot) o;
                        ///TODO start activity for this document
                    });
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

}
