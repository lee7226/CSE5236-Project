package project.cse5236.parleypirate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Taken from https://github.com/firebase/snippets-android/blob/f085cb49c21313a5d79730c025925a0b5f1610eb/auth/app/src/main/java/com/google/firebase/quickstart/auth/FirebaseUIActivity.java
 */

public class FirebaseUIActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "FirebaseUIActivity";

    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        Intent intent = getIntent();
        if(intent!=null){
            Bundle extras = intent.getExtras();
            if(extras!=null){
                if(extras.containsKey(getString(R.string.snackbar))) {
                    Snackbar.make(findViewById(R.id.firebase_ui_coordinator_layout), (String) extras.get(getString(R.string.snackbar)), Snackbar.LENGTH_SHORT).show();
                }
            }
        }

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInIntent();
            }
        });
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
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
                            } else {
                                Log.d(TAG, "Creating user");
                                createUser();
                            }
                        }else{
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                startActivity(new Intent(FirebaseUIActivity.this, MainActivity.class));
                finish();

                // ...
            } else {
                if(response==null) {
                    Snackbar.make(findViewById(R.id.firebase_ui_coordinator_layout), R.string.login_cancelled, Snackbar.LENGTH_SHORT).show();
                }else{
                    CharSequence cs = getString(R.string.login_error,response.getError().getErrorCode());
                    Snackbar.make(findViewById(R.id.firebase_ui_coordinator_layout), cs, Snackbar.LENGTH_SHORT).show();
                }
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    // [END auth_fui_result]


    /*
    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.my_great_logo)      // Set logo drawable
                        .setTheme(R.style.MySuperAppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }
    */

    private void createUser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String,Object> userAsMap = new HashMap<>();
        userAsMap.put("displayname",user.getDisplayName());
        userAsMap.put("email",user.getEmail());
        db.collection("users")
                .add(userAsMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @SuppressLint("LogNotTimber")
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("LogNotTimber")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        // [START auth_fui_pp_tos]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_pp_tos]
    }
}
