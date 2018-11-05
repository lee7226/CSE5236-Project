package project.cse5236.parleypirate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ViewMeetingsActivity extends AppCompatActivity {

    private static final String TAG = "ViewMeetingsActivity";
    private static final String USERS = "/users/";

    private ListView mListView;

    private ArrayList<String> data;

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meetings);

        addMeetingsToListView();
    }

    private void addMeetingsToListView() {
        data = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference meetingsRef = db.collection("meetings");
        DocumentReference userRef = db.document(USERS+UserDatabaseId.getDbId());
        final Query query = meetingsRef.whereArrayContains("members", userRef);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot qs = task.getResult();
                Log.d(TAG, "found "+qs.size()+" meetings");
                for (DocumentSnapshot document : qs.getDocuments()) {
                    String title = (String) document.get("title");
                    Date starttime = (Date) document.get("starttime");
                    Date endtime = (Date) document.get("endtime");
                    data.add(title+"\n"+starttime.toString()+" to "+endtime.toString());
                }
                mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
                mListView = findViewById(R.id.meetings_listview);
                mListView.setAdapter(mAdapter);
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

}
