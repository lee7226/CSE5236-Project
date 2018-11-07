package project.cse5236.parleypirate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class MeetingTitleActivity extends AppCompatActivity {

    private Button mSetDateButton;
    private EditText mTitleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_title);

        mSetDateButton = findViewById(R.id.set_date_button);
        mSetDateButton.setOnClickListener(v->{
            if(v.getId()==R.id.set_date_button){
                startStartDateActivity();
            }
        });

        mTitleEditText = findViewById(R.id.meeting_title_edit_text);
    }

    private void startStartDateActivity() {
        String title = mTitleEditText.getText().toString();
        if(!title.equals("")){
            Intent startDateIntent = new Intent(MeetingTitleActivity.this,StartDateActivity.class);
            startDateIntent.putExtra(getString(R.string.title),title);
            startActivity(startDateIntent);
            finish();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MeetingTitleActivity.this);
            builder.setMessage(R.string.error_empty_title_message).setTitle(R.string.error_empty_title_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                //do nothing
            }).create().show();
        }
    }
}
