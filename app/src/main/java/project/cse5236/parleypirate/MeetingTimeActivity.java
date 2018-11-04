package project.cse5236.parleypirate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MeetingTimeActivity extends AppCompatActivity {

    private static final int RC_CREATE_MEETING = 124;
    private static final String TAG = "MeetingTimeActivity";

    private static final int HOUR_TO_MS_CONVERSION = 3600000;

    private Button mCreateMeetingButton;
    private Button mSetLocationButton;
    private Spinner mEndTimeSpinner;
    private Spinner mStartTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_time);

        mStartTimeSpinner = findViewById(R.id.time_starttime);
        mEndTimeSpinner = findViewById(R.id.time_endtime);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStartTimeSpinner.setAdapter(adapter);
        mEndTimeSpinner.setAdapter(adapter);

        mSetLocationButton = findViewById(R.id.button_set_location);
        mSetLocationButton.setOnClickListener(v -> {
            int id = v.getId();
            if(id==R.id.button_set_location) {
                Intent setLocationIntent = new Intent(MeetingTimeActivity.this,SelectLocationActivity.class);
                ///TODO send meeting object to the activity
                startActivity(setLocationIntent);
            }
        });

        mCreateMeetingButton = findViewById(R.id.button_create_meeting);
        mCreateMeetingButton.setOnClickListener(v -> {
            int id = v.getId();
            if(id==R.id.button_create_meeting) {
                if(mStartTimeSpinner.getSelectedItemId()<mEndTimeSpinner.getSelectedItemId()) {
                    createMeeting();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MeetingTimeActivity.this);
                    builder.setMessage(R.string.error_invalid_time_message).setTitle(R.string.error_invalid_time_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }
            }
        });
    }


    public void createMeeting(){
        Intent callingIntent = getIntent();
        if(callingIntent != null && (callingIntent.hasExtra(getString(R.string.start_date)) && getIntent().hasExtra(getString(R.string.end_date)))){
            //convert the times to appropriate Timestamps

            Date startDate = (Date)callingIntent.getExtras().get(getString(R.string.start_date));
            startDate.setTime(startDate.getTime()+getTimeInMs(mStartTimeSpinner.getSelectedItemPosition()));
            Date endDate = (Date) callingIntent.getExtras().get(getString(R.string.end_date));
            endDate.setTime(endDate.getTime()+getTimeInMs(mEndTimeSpinner.getSelectedItemPosition()));
            //create the meeting object
            Meeting meeting = new Meeting(new Timestamp(startDate),new Timestamp(endDate));

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("meetings")
                    .add(meeting.toJson())
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        returnToMenu("Arr!  Successfully created a parley!");
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);
                        returnToMenu("Failed to create parley, shiver me timbers! Please try again later.");
                    });
        }
    }

    private long getTimeInMs(int hour) {
        return hour * HOUR_TO_MS_CONVERSION;
    }

    private void returnToMenu(String message){
        Intent menuIntent = new Intent(MeetingTimeActivity.this, MainActivity.class);
        menuIntent.putExtra(getString(R.string.snackbar),message);
        startActivity(menuIntent);
        finish();
    }

}
