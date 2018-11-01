package project.cse5236.parleypirate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                gc.set(year,month,dayOfMonth);
            }
        });

        mNextButton = findViewById(R.id.startdate_next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDateActivity();
            }
        });

    }

    private void endDateActivity(){
        //clear out the clock time
        gc.clear(GregorianCalendar.HOUR);
        gc.clear(GregorianCalendar.MINUTE);
        gc.clear(GregorianCalendar.SECOND);
        gc.clear(GregorianCalendar.MILLISECOND);
        gc.clear(GregorianCalendar.AM_PM);
        Intent endDateIntent = new Intent(StartDateActivity.this,EndDateActivity.class);
        endDateIntent.putExtra(getString(R.string.start_date),gc.getTime());
        startActivity(endDateIntent);
        finish();
    }
}
