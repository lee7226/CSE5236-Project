package project.cse5236.parleypirate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InviteGroupsMeetingActivity extends AppCompatActivity {

    private static final String TAG = "InviteGroupsMeeting";
    private Button mInviteGroupsMeetingButton;
    private EditText mInviteGroupMeetingEditText;
    private TextView mCurrentMembersTextView;

    private List<String> currentUsers;

    private String meetingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_groups_meeting);

        initializeCurrentMembersTextView();

        mInviteGroupMeetingEditText = findViewById(R.id.invite_group_edittext);

        mInviteGroupsMeetingButton = findViewById(R.id.invite_group_button);
        mInviteGroupsMeetingButton.setOnClickListener(v->{
            if(v.getId()==R.id.invite_group_button){
                if(!mInviteGroupMeetingEditText.getText().toString().equals("")) {
                    inviteMembers();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InviteGroupsMeetingActivity.this);
                    builder.setMessage(R.string.error_empty_invite_group_name).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }
            }
        });
    }

    private void inviteMembers() {
        String groupName = mInviteGroupMeetingEditText.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("groups");
        final Query query = usersRef.whereEqualTo("title", groupName).limit(1);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot qs = task.getResult();
                if (qs.size() > 0) {
                    Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                    addGroupMembers(db,qs,groupName);
                } else {
                    Log.d(TAG, "User not found");
                    showNotFoundDialog();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void addGroupMembers(FirebaseFirestore db, QuerySnapshot qs, String groupName) {
        String groupId = qs.getDocuments().get(0).getId();
        DocumentReference dr = db.document("/groups/"+groupId);
        dr.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot groupDs = task.getResult();
                List<DocumentReference> members = (List<DocumentReference>) groupDs.get("members");
                if(members.size()>0) {
                    boolean success = getAndAddGroupMembers(members);
                    if(success){
                        showSuccessDialog(groupName);
                    }else {
                        showFailureDialog();
                    }
                }
            }else{
                Log.e(TAG,"Error getting group");
            }
        });
    }

    private boolean getAndAddGroupMembers(List<DocumentReference> members) {
        AtomicBoolean success = new AtomicBoolean(true);
        for (DocumentReference member : members) {
            member.get().addOnCompleteListener(memberTask -> {
                if (memberTask.isSuccessful()) {
                    DocumentSnapshot memberDs = memberTask.getResult();
                    String email = (String)memberDs.get("email");
                    if(!currentUsers.contains(email)) {
                        updateCurrentMembersTextView(email);
                        currentUsers.add(email);
                    }
                }else{
                    success.set(false);
                }
            });
        }
        return success.get();
    }

    private void showSuccessDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteGroupsMeetingActivity.this);
        builder.setMessage(getString(R.string.success_invite_group,title)).setTitle(R.string.success_invite_member_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void showFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteGroupsMeetingActivity.this);
        builder.setMessage(R.string.failure_invite_group).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void showNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteGroupsMeetingActivity.this);
        builder.setMessage(R.string.failure_find_group).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void updateCurrentMembersTextView(String email) {
        mCurrentMembersTextView.setText(mCurrentMembersTextView.getText().toString() + "\n" + email);
    }

    private void initializeCurrentMembersTextView() {
        mCurrentMembersTextView = findViewById(R.id.current_members_textview);
        mCurrentMembersTextView.setText(R.string.current_members);
        currentUsers = new ArrayList<>();
        Intent callingIntent = getIntent();
        if(callingIntent!=null && callingIntent.hasExtra(getString(R.string.meetingId))){
            meetingId = (String) callingIntent.getExtras().get(getString(R.string.meetingId));
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dr = db.document("/meetings/"+meetingId);
            dr.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot meetingDs = task.getResult();
                    List<DocumentReference> members = (List<DocumentReference>) meetingDs.get("members");
                    if(members.size()>0) {
                        getAndAddMembersTextView(members);
                    }
                }else{
                    Log.e(TAG,"Error getting meeting");
                }
            });
        }

    }

    private void getAndAddMembersTextView(List<DocumentReference> members) {
        for (DocumentReference member : members) {
            member.get().addOnCompleteListener(memberTask -> {
                if (memberTask.isSuccessful()) {
                    DocumentSnapshot memberDs = memberTask.getResult();
                    String email = (String)memberDs.get("email");
                    updateCurrentMembersTextView(email);
                    currentUsers.add(email);
                }
            });
        }
    }
}
