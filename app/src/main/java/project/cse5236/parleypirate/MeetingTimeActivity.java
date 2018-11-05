package project.cse5236.parleypirate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Date;

public class MeetingTimeActivity extends AppCompatActivity {

    private static final int RC_CREATE_MEETING = 124;
    private static final String TAG = "MeetingTimeActivity";

    private static final int HOUR_TO_MS_CONVERSION = 3600000;

    private Button mSelectLocationButton;
    private Spinner mEndTimeSpinner;
    private Spinner mStartTimeSpinner;

    private Date startDate;
    private Date endDate;

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

        mSelectLocationButton = findViewById(R.id.button_select_location);
        mSelectLocationButton.setOnClickListener(v -> {
            int id = v.getId();
            if(id==R.id.button_select_location) {
                if(mStartTimeSpinner.getSelectedItemId()<mEndTimeSpinner.getSelectedItemId()) {
                    startSelectLocationActivity();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MeetingTimeActivity.this);
                    builder.setMessage(R.string.error_invalid_time_message).setTitle(R.string.error_invalid_time_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }

            }
        });
    }

    public void startSelectLocationActivity(){
        Intent callingIntent = getIntent();
        if(callingIntent != null && (callingIntent.hasExtra(getString(R.string.start_date)) && getIntent().hasExtra(getString(R.string.end_date)) && getIntent().hasExtra(getString(R.string.title)))){
            //add the correct times to the dates
            startDate = (Date)callingIntent.getExtras().get(getString(R.string.start_date));
            startDate.setTime(startDate.getTime()+getTimeInMs(mStartTimeSpinner.getSelectedItemPosition()));
            endDate = (Date) callingIntent.getExtras().get(getString(R.string.end_date));
            endDate.setTime(endDate.getTime()+getTimeInMs(mEndTimeSpinner.getSelectedItemPosition()));
            Intent setLocationIntent = new Intent(MeetingTimeActivity.this,SelectLocationActivity.class);
            setLocationIntent.putExtra(getString(R.string.title),(String)callingIntent.getExtras().get(getString(R.string.title)));
            setLocationIntent.putExtra(getString(R.string.start_date),startDate);
            setLocationIntent.putExtra(getString(R.string.end_date),endDate);
            startActivity(setLocationIntent);
            finish();
        }else{
            //somehow the intent didn't have the dates
            AlertDialog.Builder builder = new AlertDialog.Builder(MeetingTimeActivity.this);
            builder.setMessage(R.string.error_getting_intent_extras).setTitle(R.string.error_importing_time)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> returnToMenu(getString(R.string.snackbar_failed_to_create))).create().show();
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
