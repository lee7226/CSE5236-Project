package project.cse5236.parleypirate;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class InviteMembersActivity extends AppCompatActivity {

    private static final String TAG = "InviteUsersActivity";
    private TextView mCurrentMembersTextView;
    private Button mInviteMemberButton;
    private EditText mInviteMemberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users);

        initializeCurrentMembersTextView();

        mInviteMemberEditText = findViewById(R.id.invite_member_edittext);

        mInviteMemberButton = findViewById(R.id.invite_member_button);
        mInviteMemberButton.setOnClickListener(v->{
            if(v.getId()==R.id.invite_member_button){
                if(!mInviteMemberEditText.getText().equals("")) {
                    inviteMember();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersActivity.this);
                    builder.setMessage(R.string.error_empty_invite_email).setTitle(R.string.error_empty_invite_email_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }
            }
        });
    }

    private void inviteMember() {
        String email = mInviteMemberEditText.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        final Query query = usersRef.whereEqualTo("email",email).limit(1);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot qs = task.getResult();
                if (qs.size()>0) {
                    Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                    List<DocumentSnapshot> docRefs = qs.getDocuments();
                    String id = docRefs.get(0).getId();
                    updateDatabaseUserDisplayName(id);
                } else {
                    Log.d(TAG, "User not found");
                }
            }else{
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void initializeCurrentMembersTextView() {
        mCurrentMembersTextView = findViewById(R.id.current_members_textview);
        Intent callingIntent = getIntent();
        if(callingIntent!=null && callingIntent.hasExtra("meeting")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dr = db.document("/meetings/"+callingIntent.getExtras().get("meeting"));
            StringBuilder sb = new StringBuilder(getString(R.string.current_members));
            dr.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot meetingDs = task.getResult();
                    List<DocumentReference> members = (List<DocumentReference>) meetingDs.get("users");

                    if(members.size()>0) {
                        for (DocumentReference member : members) {
                            member.get().addOnCompleteListener(memberTask -> {
                                if (memberTask.isSuccessful()) {
                                    DocumentSnapshot memberDs = memberTask.getResult();
                                    sb.append(memberDs.get("email"));
                                }
                            });
                        }
                    }
                    mCurrentMembersTextView.setText(sb.toString());
                }else{
                    Log.e(TAG,"Error getting meeting");
                }
            });
            mCurrentMembersTextView.setText(sb.toString());
        }
    }


}
