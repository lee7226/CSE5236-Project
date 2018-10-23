package project.cse5236.parleypirate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements DeleteAccountDialogFragment.DeleteAccountDialogFragmentListener {

    private static final String TAG = "MainActivity";
    private static final String DELETE_ACCOUNT_DIALOG_FRAGMENT_TAG = "DeleteAccountDialogFragment";
    private static final String UPDATE_DISPLAYNAME_DIALOG_FRAGMENT_TAG = "UpdateDisplayNameDialogFragment";
    public static final String SUCCESSFULLY_DELETED_ACCOUNT = "Successfully deleted account";
    public static final String SUCCESSFULLY_LOGGED_OUT = "Successfully logged out";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity",getString(R.string.onCreateLog));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_logout){
            signOut();
        }else if(id == R.id.action_delete_account){
            confirmDelete();
        }else if(id == R.id.action_update_displayname){
            updateDisplayName();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDisplayName() {
        UpdateDisplayNameDialogFragment dialog = new UpdateDisplayNameDialogFragment();
        dialog.show(getSupportFragmentManager(), UPDATE_DISPLAYNAME_DIALOG_FRAGMENT_TAG);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        returnToLogin(SUCCESSFULLY_LOGGED_OUT);
                    }
                });
    }

    public void confirmDelete(){
        DeleteAccountDialogFragment dialog = new DeleteAccountDialogFragment();
        dialog.show(getSupportFragmentManager(), DELETE_ACCOUNT_DIALOG_FRAGMENT_TAG);
    }

    public void delete() {
        //delete the authenticated user
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        returnToLogin(SUCCESSFULLY_DELETED_ACCOUNT);
                    }
                });
        //delete user from the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference usersRef = db.collection("users");
        final Query query = usersRef.whereEqualTo("email",user.getEmail()).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot qs = task.getResult();
                    if (qs.size()>0) {
                        Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                        qs.getDocuments().remove(0);
                    } else {
                        Log.d(TAG, "User not found");
                    }
                }else{
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        delete();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void returnToLogin(String message){
        Intent loginIntent = new Intent(MainActivity.this,FirebaseUIActivity.class);
        loginIntent.putExtra(getString(R.string.snackbar),message);
        startActivity(loginIntent);
        finish();
    }
}
