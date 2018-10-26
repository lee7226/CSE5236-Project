package project.cse5236.parleypirate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateMeetingActivity extends AppCompatActivity {

    private static final int RC_CREATE_MEETING = 124;
    private static final String TAG = "CreateMeetingActivity";

    private Button mCreateMeetingButton;
    private EditText mEndTimeEdit;
    private EditText mStartTimeEdit;
    private EditText mMembersEdit;
    private EditText mAvailablilityEdit;
    private EditText mLocationEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_meeting);

        mStartTimeEdit = findViewById(R.id.date_starttime);
        mEndTimeEdit = findViewById(R.id.date_endtime);
        mMembersEdit = findViewById(R.id.string_members_input);
        mAvailablilityEdit = findViewById(R.id.string_availabilities_input);
        mLocationEdit = findViewById(R.id.string_location_input);


        mCreateMeetingButton = findViewById(R.id.button_create_meeting);
        mCreateMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if(id==R.id.button_create_meeting) {
                    createMeeting();
                }
            }
        });
    }

    public void createMeeting(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> meetingAsMap = new HashMap<>();

        String membersText = mMembersEdit.getText().toString();
        String locationText = mLocationEdit.getText().toString();
        String availabilityText = mAvailablilityEdit.getText().toString();
        String startTimeText = mStartTimeEdit.getText().toString();
        String endTimeText = mEndTimeEdit.getText().toString();

        meetingAsMap.put("members", membersText);
        meetingAsMap.put("location", locationText);
        meetingAsMap.put("availabilities", availabilityText);
        meetingAsMap.put("starttime",  startTimeText);
        meetingAsMap.put("endtime", endTimeText);
        db.collection("meetings")
                .add(meetingAsMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @SuppressLint("LogNotTimber")
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        returnToMenu("Arr!  Successfully created a parley!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("LogNotTimber")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        returnToMenu("Failed to create parley, shiver me timbers! Please try again later.");
                    }
                });
    }

    private void returnToMenu(String message){
        Intent menuIntent = new Intent(CreateMeetingActivity.this, MainActivity.class);
        menuIntent.putExtra(getString(R.string.snackbar),message);
        startActivity(menuIntent);
        finish();
    }

}
