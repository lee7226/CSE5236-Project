package project.cse5236.parleypirate;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class CreateGroupActivity extends AppCompatActivity {

    private static final String TAG = "CreateGroupActivity";
    private Button mInviteMembersButton;
    private EditText mTitleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mInviteMembersButton = findViewById(R.id.set_date_button);
        mInviteMembersButton.setOnClickListener(v->{
            if(v.getId()==R.id.set_date_button){
                createGroup();
            }
        });

        mTitleEditText = findViewById(R.id.meeting_title_edit_text);
    }

    private void createGroup() {
        String title = mTitleEditText.getText().toString();
        if(!title.equals("")){
            Group group = new Group();
            group.addMember(UserDatabaseId.getDbId());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("groups")
                    .add(group.toJson())
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        returnToMenu("Arr! Successfully created a group!");
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);
                        returnToMenu(getString(R.string.snackbar_failed_to_create_group));
                    });
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
            builder.setMessage(R.string.error_empty_title_message).setTitle(R.string.error_empty_title_title).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                //do nothing
            }).create().show();
        }
    }

    private void returnToMenu(String message) {
        Intent menuIntent = new Intent(CreateGroupActivity.this, MainActivity.class);
        menuIntent.putExtra(getString(R.string.snackbar),message);
        startActivity(menuIntent);
        finish();
    }

}
