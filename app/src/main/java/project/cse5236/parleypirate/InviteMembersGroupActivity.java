package project.cse5236.parleypirate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InviteMembersGroupActivity extends AppCompatActivity {

    private static final String TAG = "InviteMembersGroup";
    private TextView mCurrentMembersTextView;
    private Button mInviteMemberButton;
    private EditText mInviteMemberEditText;

    private List<String> currentUsers;

    private String groupId;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_invite_members);

        mProgressBar = findViewById(R.id.progressBar_InviteMembersFragment);
        mProgressBar.setVisibility(View.VISIBLE);
        initializeCurrentMembersTextView();

        mInviteMemberEditText = findViewById(R.id.invite_member_edittext);

        mInviteMemberButton = findViewById(R.id.invite_member_button);
        mInviteMemberButton.setOnClickListener(v->{
            if(v.getId()==R.id.invite_member_button){
                mProgressBar.setVisibility(View.VISIBLE);
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if(ni != null && ni.isConnected()) {
                    if(!mInviteMemberEditText.getText().toString().equals("")) {
                        inviteMember();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersGroupActivity.this);
                        builder.setMessage(R.string.error_empty_invite_email).setTitle(R.string.error_empty_invite_email_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            //do nothing
                        }).create().show();
                    }
                }else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersGroupActivity.this);
                    builder.setMessage(R.string.error_no_network).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
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
                        addMemberToDatabase(db, qs, email);
                    } else {
                        Log.d(TAG, "User not found");
                        showNotFoundDialog();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }else {
            showAlreadyInGroupDialog();
        }
    }

    private void showAlreadyInGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersGroupActivity.this);
        builder.setMessage(R.string.failure_already_in_group).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void showNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersGroupActivity.this);
        builder.setMessage(R.string.failure_find_member).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void addMemberToDatabase(FirebaseFirestore db, QuerySnapshot qs,String email) {
        List<DocumentSnapshot> docRefs = qs.getDocuments();
        String id = docRefs.get(0).getId();
        DocumentReference group = db.document("/groups/"+groupId);
        group.get().addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                DocumentSnapshot ds = task.getResult();
                List<DocumentReference> members = (List<DocumentReference>) ds.get("members");
                members.add(db.document("/users/"+id));
                db.document("/groups/"+groupId).update("members",members).addOnCompleteListener(addTask->{
                    if(addTask.isSuccessful()){
                        Log.d(TAG, "added member successfully");
                        updateCurrentMembersTextView(email);
                        showSuccessDialog(email);
                    }else {
                        Log.e(TAG,"Failed to add member to group");
                        showFailureDialog();
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                });
            }
        });
    }

    private void showFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersGroupActivity.this);
        builder.setMessage(R.string.failure_invite_member).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void updateCurrentMembersTextView(String email) {
        mCurrentMembersTextView.setText(mCurrentMembersTextView.getText().toString() + "\n" + email);
    }

    private void showSuccessDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMembersGroupActivity.this);
        builder.setMessage(getString(R.string.success_invite_member,email)).setTitle(R.string.success_invite_member_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //do nothing
        }).create().show();
    }

    private void initializeCurrentMembersTextView() {
        mCurrentMembersTextView = findViewById(R.id.current_members_textview);
        mCurrentMembersTextView.setText(R.string.current_members);
        currentUsers = new ArrayList<>();
        Intent callingIntent = getIntent();
        if(callingIntent!=null && callingIntent.hasExtra("groupId")){
            groupId = (String) callingIntent.getExtras().get("groupId");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dr = db.document("/groups/"+groupId);
            dr.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot groupDs = task.getResult();
                    List<DocumentReference> members = (List<DocumentReference>) groupDs.get("members");
                    if(members.size()>0) {
                        mProgressBar.setVisibility(View.VISIBLE);
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
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }else{
                    Log.e(TAG,"Error getting group");
                }
                mProgressBar.setVisibility(View.INVISIBLE);
            });
        }
        mProgressBar.setVisibility(View.INVISIBLE);

    }
}
