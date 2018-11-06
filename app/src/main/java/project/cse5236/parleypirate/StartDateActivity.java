package project.cse5236.parleypirate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.GregorianCalendar;

public class StartDateActivity extends AppCompatActivity {

    private CalendarView mCalendarView;
    private Button mNextButton;

    private GregorianCalendar gc = new GregorianCalendar();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_date);

        mCalendarView = findViewById(R.id.startdate_calendar);

        mCalendarView.setMinDate(gc.getTimeInMillis());

        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> gc.set(year,month,dayOfMonth));

        mNextButton = findViewById(R.id.startdate_next_button);
        mNextButton.setOnClickListener(v -> {
            if(v.getId()==R.id.startdate_next_button) {
                endDateActivity();
            }
        });

    }

    private void endDateActivity(){
        //clear out the clock time
        gc.set(GregorianCalendar.HOUR_OF_DAY,0);
        gc.set(GregorianCalendar.HOUR,0);
        gc.set(GregorianCalendar.MINUTE,0);
        gc.set(GregorianCalendar.SECOND,0);
        gc.set(GregorianCalendar.MILLISECOND,0);
        gc.set(GregorianCalendar.AM_PM,0);
        Intent callingIntent = getIntent();
        if(callingIntent != null && callingIntent.hasExtra(getString(R.string.title))){
            Intent endDateIntent = new Intent(StartDateActivity.this,EndDateActivity.class);
            endDateIntent.putExtra(getString(R.string.title),(String)callingIntent.getExtras().get(getString(R.string.title)));
            endDateIntent.putExtra(getString(R.string.start_date),gc.getTime());
            startActivity(endDateIntent);
            finish();
        }else{
            //somehow the intent didn't have the dates
            AlertDialog.Builder builder = new AlertDialog.Builder(StartDateActivity.this);
            builder.setMessage(R.string.error_getting_intent_extras).setTitle(R.string.error_importing_time)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> returnToMenu(getString(R.string.snackbar_failed_to_create))).create().show();
        }

    }

    private void returnToMenu(String message) {
        Intent menuIntent = new Intent(StartDateActivity.this, MainActivity.class);
        menuIntent.putExtra(getString(R.string.snackbar),message);
        startActivity(menuIntent);
        finish();
    }
}
