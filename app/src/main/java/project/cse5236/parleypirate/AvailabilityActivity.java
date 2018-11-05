package project.cse5236.parleypirate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AvailabilityActivity extends AppCompatActivity {

    private Spinner mAvailabilitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        mAvailabilitySpinner = findViewById(R.id.spinnerDayOfWeek);
        String[] items = new String[]{"Monday", "Tuesday", "Wednesday"};

        int test = android.R.layout.simple_spinner_dropdown_item;

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAvailabilitySpinner.setAdapter(adapter);

        String stuff = "does crash on setAdapter";
    }
}
