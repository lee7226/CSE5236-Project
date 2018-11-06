package project.cse5236.parleypirate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.Date;
import java.util.GregorianCalendar;

public class EndDateActivity extends AppCompatActivity {

    private CalendarView mCalendarView;
    private Button mNextButton;

    private GregorianCalendar gc = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_date);

        mCalendarView = findViewById(R.id.enddate_calendar);
        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> gc.set(year,month,dayOfMonth));
        Intent callingIntent = getIntent();
        if(callingIntent != null && callingIntent.hasExtra(getString(R.string.start_date))) {
            Date minDate = (Date)callingIntent.getExtras().get(getString(R.string.start_date));
            mCalendarView.setMinDate(minDate.getTime());
            gc.setTime(minDate);
        }

        mNextButton = findViewById(R.id.enddate_next_button);
        mNextButton.setOnClickListener(v -> {
            if(v.getId()==R.id.enddate_next_button) {
                startTimeActivity();
            }
        });
    }

    private void startTimeActivity(){
        //clear out the clock time
        gc.set(GregorianCalendar.HOUR_OF_DAY,0);
        gc.set(GregorianCalendar.HOUR,0);
        gc.set(GregorianCalendar.MINUTE,0);
        gc.set(GregorianCalendar.SECOND,0);
        gc.set(GregorianCalendar.MILLISECOND,0);
        gc.set(GregorianCalendar.AM_PM,0);
        Intent callingIntent = getIntent();
        if(callingIntent != null && (callingIntent.hasExtra(getString(R.string.start_date))&&callingIntent.hasExtra(getString(R.string.title)))){
            Intent enterTimesIntent = new Intent(EndDateActivity.this,MeetingTimeActivity.class);
            enterTimesIntent.putExtra(getString(R.string.end_date),gc.getTime());
            enterTimesIntent.putExtra(getString(R.string.start_date),(Date)callingIntent.getExtras().get(getString(R.string.start_date)));
            enterTimesIntent.putExtra(getString(R.string.title),(String)callingIntent.getExtras().get(getString(R.string.title)));
            startActivity(enterTimesIntent);
            finish();
        }else{
            //somehow the intent didn't have the dates
            AlertDialog.Builder builder = new AlertDialog.Builder(EndDateActivity.this);
            builder.setMessage(R.string.error_getting_intent_extras).setTitle(R.string.error_importing_time)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> returnToMenu(getString(R.string.snackbar_failed_to_create))).create().show();
        }

    }

    private void returnToMenu(String message) {
        Intent menuIntent = new Intent(EndDateActivity.this, MainActivity.class);
        menuIntent.putExtra(getString(R.string.snackbar),message);
        startActivity(menuIntent);
        finish();
    }
}
