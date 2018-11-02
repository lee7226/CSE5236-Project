package project.cse5236.parleypirate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        gc.clear(GregorianCalendar.HOUR);
        gc.clear(GregorianCalendar.MINUTE);
        gc.clear(GregorianCalendar.SECOND);
        gc.clear(GregorianCalendar.MILLISECOND);
        gc.clear(GregorianCalendar.AM_PM);
        Intent callingIntent = getIntent();
        if(callingIntent != null && callingIntent.hasExtra(getString(R.string.start_date))){
            Intent enterTimesIntent = new Intent(EndDateActivity.this,MeetingTimeActivity.class);
            enterTimesIntent.putExtra(getString(R.string.end_date),gc.getTime());
            enterTimesIntent.putExtra(getString(R.string.start_date),(Date)callingIntent.getExtras().get(getString(R.string.start_date)));
            startActivity(enterTimesIntent);
            finish();
        }
    }
}
