package project.cse5236.parleypirate;

import android.content.Intent;
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

public class ViewGroupsActivity extends AppCompatActivity {

    private static final String TAG = "ViewGroupsActivity";
    private static final String USERS = "/users/";

    private ListView mListView;

    private List<DocumentSnapshot> groups;

    private GroupListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_listview);

        addGroupsToListView();
    }

    private void addGroupsToListView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupsRef = db.collection("groups");
        DocumentReference userRef = db.document(USERS+UserDatabaseId.getDbId());
        final Query query = groupsRef.whereArrayContains("groups", userRef);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot qs = task.getResult();
                if(qs!=null && qs.size()>0) {
                    Log.d(TAG, "found " + qs.size() + " groups");
                    groups = new ArrayList<>();
                    groups.addAll(qs.getDocuments());
                    mAdapter = new GroupListAdapter(this, groups);
                    mListView = findViewById(R.id.listview);
                    mListView.setAdapter(mAdapter);
                    mListView.setClickable(true);
                    mListView.setOnItemClickListener((parent, view, position, id) -> {
                        Object o = mListView.getItemAtPosition(position);
                        DocumentSnapshot ds = (DocumentSnapshot) o;
                        Intent inviteUsersIntent = new Intent(ViewGroupsActivity.this,InviteMembersGroupActivity.class);
                        inviteUsersIntent.putExtra("groupId",ds.getId());
                        startActivity(inviteUsersIntent);
                    });
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}
