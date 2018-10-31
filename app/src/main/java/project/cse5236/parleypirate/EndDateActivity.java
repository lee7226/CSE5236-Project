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
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                gc.set(year,month,dayOfMonth);
            }
        });

        mNextButton = findViewById(R.id.enddate_next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeActivity();
            }
        });
    }

    private void startTimeActivity(){
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
