package project.cse5236.parleypirate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

public class MeetingOptionsActivity extends AppCompatActivity {

    private static final String TAG = "MeetingOptionsActivity";
    private Button mInviteMembersButton;
    private Button mInviteGroupsButton;
    private Button mViewAvailabilitiesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_options);

        mInviteMembersButton = findViewById(R.id.button_invite_members_to_meeting);
        mInviteMembersButton.setOnClickListener(v->{
            if(v.getId()==R.id.button_invite_members_to_meeting){
                if(callingIntentValid()){
                    Intent inviteMembersIntent = new Intent(MeetingOptionsActivity.this,InviteMembersMeetingActivity.class);
                    inviteMembersIntent.putExtra(getString(R.string.meetingId),(String)getIntent().getExtras().get(getString(R.string.meetingId)));
                    startActivity(inviteMembersIntent);
                }else{
                    Log.e(TAG,"Something went wrong with intent");
                }
            }
        });

        mInviteGroupsButton = findViewById(R.id.button_invite_groups_to_meeting);
        mInviteGroupsButton.setOnClickListener(v->{
            if(v.getId()==R.id.button_invite_groups_to_meeting){
                if(callingIntentValid()){
                    Intent inviteMembersIntent = new Intent(MeetingOptionsActivity.this,InviteGroupsMeetingActivity.class);
                    inviteMembersIntent.putExtra(getString(R.string.meetingId),(String)getIntent().getExtras().get(getString(R.string.meetingId)));
                    startActivity(inviteMembersIntent);
                }else{
                    Log.e(TAG,"Something went wrong with intent");
                }
            }
        });

        mViewAvailabilitiesButton = findViewById(R.id.button_view_meeting_availability);
        mViewAvailabilitiesButton.setOnClickListener(v->{
            if(v.getId()==R.id.button_view_meeting_availability){
                //TODO implement show activities class
                /*
                if(callingIntentValid()){
                    Intent inviteMembersIntent = new Intent(MeetingOptionsActivity.this,InviteMembersMeetingActivity.class);
                    inviteMembersIntent.putExtra(getString(R.string.meetingId),(String)getIntent().getExtras().get(getString(R.string.meetingId)));
                    startActivity(inviteMembersIntent);
                }
                */
            }
        });
    }

    private boolean callingIntentValid() {
        Intent callingIntent = getIntent();
        return callingIntent != null && callingIntent.hasExtra(getString(R.string.meetingId));
    }
}
