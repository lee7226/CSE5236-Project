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

public class InviteMembersMeetingActivity extends AppCompatActivity {

    private static final String TAG = "InviteMembersMeeting";
    private TextView mCurrentMembersTextView;
    private Button mInviteMemberButton;
    private Button mViewAvailableTimesButton;
    private EditText mInviteMemberEditText;

    private List<String> currentUsers;

    private String meetingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_invite_members);

        initializeCurrentMembersTextView();

        mInviteMemberEditText = findViewById(R.id.invite_member_edittext);
        mViewAvailableTimesButton = findViewById(R.id.view_available_times_button);

        mInviteMemberButton = findViewById(R.id.invite_member_button);
        mInviteMemberButton.setOnClickListener(v->{
            if(v.getId()==R.id.invite_member_button){
                if(!mInviteMemberEditText.getText().toString().equals("")) {
                    inviteMember();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersMeetingActivity.this);
                    builder.setMessage(R.string.error_empty_invite_email).setTitle(R.string.error_empty_invite_email_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }
            }
        });

        mViewAvailableTimesButton.setOnClickListener(v->{
            if(v.getId()==R.id.view_available_times_button) {

                Intent meetingAvailabilityIntent = new Intent(InviteMembersActivity.this,MeetingAvailabilityActivity.class);

                // clunky way of doing this for now, will be more optimized in the future
                Intent callingIntent = getIntent();
                if(callingIntent!=null && callingIntent.hasExtra("meetingId")){
                    meetingId = (String) callingIntent.getExtras().get("meetingId");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference dr = db.document("/meetings/"+meetingId);
                    dr.get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot meetingDs = task.getResult();
                            List<DocumentReference> members = (List<DocumentReference>) meetingDs.get("members");
                            if(members.size()>0) {
                                List<String> userIds = new ArrayList<>();
                                for (int i = 0; i < members.size(); i++) {
                                    meetingAvailabilityIntent.putExtra("user" + i, members.get(i).getId());
                                }
                                startActivity(meetingAvailabilityIntent);
                            }
                        }else{
                            Log.e(TAG,"Error getting meeting");
                        }
                    });
                }
            }
        });
    }

    private void inviteMember() {
        String email = mInviteMemberEditText.getText().toString().toLowerCase();
        if(!currentUsers.contains(email)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");
            final Query query = usersRef.whereEqualTo("email", email).limit(1);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot qs = task.getResult();
                    if (qs.size() > 0) {
                        Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                        addMember(db, qs, email);
                    } else {
                        Log.d(TAG, "User not found");
                        showNotFoundDialog();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }else {
            showAlreadyInMeetingDialog();
        }
    }

    private void showAlreadyInMeetingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersMeetingActivity.this);
        builder.setMessage(R.string.failure_already_in_meeting).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void showNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersMeetingActivity.this);
        builder.setMessage(R.string.failure_find_member).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void addMember(FirebaseFirestore db, QuerySnapshot qs, String email) {
        List<DocumentSnapshot> docRefs = qs.getDocuments();
        String id = docRefs.get(0).getId();
        DocumentReference meeting = db.document("/meetings/"+meetingId);
        meeting.get().addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                DocumentSnapshot ds = task.getResult();
                List<DocumentReference> members = (List<DocumentReference>) ds.get("members");
                members.add(db.document("/users/"+id));
                db.document("/meetings/"+meetingId).update("members",members).addOnCompleteListener(addTask->{
                    if(addTask.isSuccessful()){
                        Log.d(TAG, "added member successfully");
                        updateCurrentMembersTextView(email);
                        showSuccessDialog(email);
                    }else {
                        Log.e(TAG,"Failed to add member to meeting");
                        showFailureDialog();
                    }
                });
            }
        });
    }

    private void showFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersMeetingActivity.this);
        builder.setMessage(R.string.failure_invite_member).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void updateCurrentMembersTextView(String email) {
        mCurrentMembersTextView.setText(mCurrentMembersTextView.getText().toString() + "\n" + email);
    }

    private void showSuccessDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersMeetingActivity.this);
        builder.setMessage(getString(R.string.success_invite_member,email)).setTitle(R.string.success_invite_member_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
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
                }else{
                    Log.e(TAG,"Error getting meeting");
                }
            });
        }
    }
}
