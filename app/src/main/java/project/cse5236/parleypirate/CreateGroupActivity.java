package project.cse5236.parleypirate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.firestore.FirebaseFirestore;

public class CreateGroupActivity extends AppCompatActivity {

    private static final String TAG = "CreateGroupActivity";
    private Button mCreateGroupButton;
    private EditText mTitleEditText;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mCreateGroupButton = findViewById(R.id.button_create_group);
        mCreateGroupButton.setOnClickListener(v->{
            if(v.getId()==R.id.button_create_group){
                mProgressBar = findViewById(R.id.progressBar_CreateGroupActivity);
                mProgressBar.setVisibility(View.VISIBLE);
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if(ni != null && ni.isConnected()) {
                    createGroup();
                }else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
                    builder.setMessage(R.string.error_no_network).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }

            }
        });

        mTitleEditText = findViewById(R.id.group_title_edit_text);
    }

    private void createGroup() {
        String title = mTitleEditText.getText().toString();
        if(!title.equals("")){
            Group group = new Group();
            group.addMember(UserDatabaseId.getDbId());
            group.setTitle(title);

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
                    })
                    .addOnCompleteListener(e -> mProgressBar.setVisibility(View.GONE));
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
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
