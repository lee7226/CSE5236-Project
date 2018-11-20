package project.cse5236.parleypirate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements DeleteAccountDialogFragment.DeleteAccountDialogFragmentListener,
        UpdateDisplayNameDialogFragment.UpdateDisplayNameDialogFragmentListener {

    private static final String TAG = "MainActivity";
    private static final String DELETE_ACCOUNT_DIALOG_FRAGMENT_TAG = "DeleteAccountDialogFragment";
    private static final String UPDATE_DISPLAYNAME_DIALOG_FRAGMENT_TAG =
            "UpdateDisplayNameDialogFragment";
    public static final String SUCCESSFULLY_DELETED_ACCOUNT = "Successfully deleted account";
    public static final String SUCCESSFULLY_LOGGED_OUT = "Successfully logged out";

    private Button mNewMeetingButton;
    private Button mEnterAvailabilityButton;
    private Button mViewMeetingsButton;
    private Button mCreateGroupButton;
    private Button mViewGroupsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity",getString(R.string.onCreateLog));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(getString(R.string.snackbar))){
            Snackbar.make(findViewById(R.id.main_activity_coordinator_layout),
                    (String) intent.getExtras().get(getString(R.string.snackbar)), Snackbar.LENGTH_SHORT).show();
        }

        mNewMeetingButton = findViewById(R.id.button_new_meeting);
        mNewMeetingButton.setOnClickListener(v -> {
            if(v.getId()==R.id.button_new_meeting) {
                startActivity(new Intent(MainActivity.this, MeetingTitleActivity.class));
            }
        });

        mViewMeetingsButton = findViewById(R.id.button_view_meetings);
        mViewMeetingsButton.setOnClickListener(v->{
            if(v.getId()==R.id.button_view_meetings) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if(ni != null && ni.isConnected()) {
                    startActivity(new Intent(MainActivity.this, ViewMeetingsActivity.class));
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.error_no_network).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }
            }
        });

        mEnterAvailabilityButton = findViewById(R.id.button_enter_availability);
        mEnterAvailabilityButton.setOnClickListener(v -> {
            if(v.getId()==R.id.button_enter_availability){
                startActivity(new Intent(MainActivity.this, AvailabilityActivity.class));
            }
        });

        mViewGroupsButton = findViewById(R.id.button_view_groups);
        mViewGroupsButton.setOnClickListener(v-> {
            if(v.getId()==R.id.button_view_groups){
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if(ni != null && ni.isConnected()) {
                    startActivity(new Intent(MainActivity.this, ViewGroupsActivity.class));
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.error_no_network).setTitle(R.string.avast).setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        //do nothing
                    }).create().show();
                }
            }
        });

        mCreateGroupButton = findViewById(R.id.button_create_group);
        mCreateGroupButton.setOnClickListener(v->{
            if(v.getId()==R.id.button_create_group){
                startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        Log.d("MainActivity",getString(R.string.onStartLog));
        super.onStart();
    }
    @Override
    public void onResume() {
        Log.d("MainActivity",getString(R.string.onResumeLog));
        super.onResume();
    }
    @Override
    public void onPause() {
        Log.d("MainActivity",getString(R.string.onPauseLog));
        super.onPause();
    }
    @Override
    public void onStop() {
        Log.d("MainActivity",getString(R.string.onStopLog));
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Log.d("MainActivity",getString(R.string.onDestroyLog));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_logout){
            signOut();
        }else if(id == R.id.action_delete_account){
            confirmDelete();
        }else if(id == R.id.action_update_displayname){
            updateDisplayNameDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDisplayNameDialog() {
        UpdateDisplayNameDialogFragment dialog = new UpdateDisplayNameDialogFragment();
        dialog.show(getSupportFragmentManager(), UPDATE_DISPLAYNAME_DIALOG_FRAGMENT_TAG);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> returnToLogin(SUCCESSFULLY_LOGGED_OUT));
    }

    public void confirmDelete(){
        DeleteAccountDialogFragment dialog = new DeleteAccountDialogFragment();
        dialog.show(getSupportFragmentManager(), DELETE_ACCOUNT_DIALOG_FRAGMENT_TAG);
    }

    private void deleteAuthenticatedUser() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(task -> returnToLogin(SUCCESSFULLY_DELETED_ACCOUNT));
    }

    private void queryAndDeleteDatabaseUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference usersRef = db.collection("users");
        final Query query = usersRef.whereEqualTo("email",user.getEmail()).limit(1);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot qs = task.getResult();
                if (qs.size()>0) {
                    Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                    List<DocumentSnapshot> docRefs = qs.getDocuments();
                    String id = docRefs.get(0).getId();
                    deleteDatabaseUser(id);
                } else {
                    Log.d(TAG, "User not found");
                }

            }else{
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void deleteDatabaseUser(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(id);
        userRef.delete().addOnSuccessListener(aVoid ->
                Log.d(TAG, "User successfully deleted!")).addOnFailureListener(e ->
                Log.w(TAG, "Error deleting document", e)).addOnCompleteListener(task ->
                deleteAuthenticatedUser());
    }

    private void queryAndUpdateDatabaseUserDisplayName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        final Query query = usersRef.whereEqualTo("email",user.getEmail()).limit(1);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot qs = task.getResult();
                if (qs.size()>0) {
                    Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                    List<DocumentSnapshot> docRefs = qs.getDocuments();
                    String id = docRefs.get(0).getId();
                    updateDatabaseUserDisplayName(id);
                } else {
                    Log.d(TAG, "User not found");
                }
            }else{
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void updateDatabaseUserDisplayName(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = db.collection("users").document(id);

        userRef.update("displayname",user.getDisplayName())
                .addOnSuccessListener(aVoid ->
                Log.d(TAG, "DocumentSnapshot successfully updated!")).addOnFailureListener(e ->
                Log.w(TAG, "Error updating document", e));
    }

    private void returnToLogin(String message){
        Intent loginIntent = new Intent(MainActivity.this,FirebaseUIActivity.class);
        loginIntent.putExtra(getString(R.string.snackbar),message);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onDeleteAccountDialogFragmentPositiveClick(DialogFragment dialog) {
        queryAndDeleteDatabaseUser();
    }

    @Override
    public void onUpdateDisplayNameDialogFragmentPositiveClick(DialogFragment dialog, String result) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                .setDisplayName(result)
                .build();
        user.updateProfile(update).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User profile display name changed");
                //once auth user is updated, update the database
                queryAndUpdateDatabaseUserDisplayName();
            }
        });
    }
}
