package project.cse5236.parleypirate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailabilityActivity extends AppCompatActivity {

    private static final String TAG = "AvailabilityActivity";

    private Spinner mAvailabilitySpinner;
    private Button[] mAvailabilityButtons;
    private Button mSaveAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mAvailabilityButtons = new Button[48];

        for (int i = 0; i < mAvailabilityButtons.length; i++) {
            int resourceId = this.getResources().getIdentifier("button_availability" + i, "id", this.getPackageName());
            mAvailabilityButtons[i] = findViewById(resourceId);
            mAvailabilityButtons[i].setBackgroundColor(getResources().getColor(R.color.colorRed));
            updateAvailabilityOnClick(mAvailabilityButtons[i], i);
        }

        CollectionReference avalRef = db.collection("availabilities");
        final Query query = avalRef.whereEqualTo("user",user.getUid());
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot qs = task.getResult();
                if (qs.size()>0) {
                    List<DocumentSnapshot> avalList = qs.getDocuments();
                    String availabilityInnerString = (String)avalList.get(0).get("availability");
                    for (int i = 0; i < mAvailabilityButtons.length; i++) {
                        if (availabilityInnerString.charAt(i) == '1') {
                            mAvailabilityButtons[i].setBackgroundColor(getResources().getColor(R.color.colorGreen));
                            mAvailabilityButtons[i].setContentDescription("1");
                        }
                    }
                    Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                } else {
                    Log.d(TAG, "user has no availability");
                }
            }else{
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        mSaveAvailability = findViewById(R.id.button_save_availability);
        mSaveAvailability.setOnClickListener(v -> saveAvailability());


        mAvailabilitySpinner = findViewById(R.id.spinnerDayOfWeek);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAvailabilitySpinner.setAdapter(adapter);
    }

    private void updateAvailability(int index) {
        if (mAvailabilityButtons[index].getContentDescription() == "1") {
            mAvailabilityButtons[index].setContentDescription("0");
            mAvailabilityButtons[index].setBackgroundColor(getResources().getColor(R.color.colorRed));
        } else {
            mAvailabilityButtons[index].setContentDescription("1");
            mAvailabilityButtons[index].setBackgroundColor(getResources().getColor(R.color.colorGreen));
        }
    }

    private void updateAvailabilityOnClick(final Button btn, final int index){
        btn.setOnClickListener(v -> updateAvailability(index));
    }

    private void saveAvailability() {
        StoreAvailability();
    }

    private String getOnScreenAvailability() {
        StringBuilder availabilityString = new StringBuilder();
        for (int i = 0; i < mAvailabilityButtons.length; i++) {
            if (mAvailabilityButtons[i].getContentDescription() == "1") {
                availabilityString.append("1");
            } else {
                availabilityString.append("0");
            }
        }
        return availabilityString.toString();
    }

    public String GetOpenAvalability(String[] avals) {
        String openAval = "";
        int[] intRepresentations = new int[avals.length];

        // convert all given avals to int then to binary
        for (int i = 0; i < avals.length; i++) {
            intRepresentations[i] = Integer.parseInt(avals[i], 2);
        }

        int bitwisedAvals = 0;

        // do bitwise or on binary avals
        for (int i = 0; i < intRepresentations.length; i++) {
            bitwisedAvals = bitwisedAvals | intRepresentations[i];
        }
        // set openAvals to the result
        openAval = Integer.toString(bitwisedAvals, 2);

        return  openAval;
    }

    public void StoreAvailability() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference avalRef = db.collection("availabilities");
        final Query query = avalRef.whereEqualTo("user",user.getUid()).limit(1);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot qs = task.getResult();
                if (qs.size()>0) {
                    Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                    List<DocumentSnapshot> docRefs = qs.getDocuments();
                    String id = docRefs.get(0).getId();
                    updateDatabaseAvailability(id, getOnScreenAvailability());
                } else {
                    Log.d(TAG, "User not found");
                    createDatabaseAvailability(getOnScreenAvailability());
                }
                Toast.makeText(getApplicationContext(), "Availability Saved!",
                Toast.LENGTH_LONG).show();
            }else{
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void updateDatabaseAvailability(String id, String aval){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference avalRef = db.collection("availabilities").document(id);

        avalRef.update("availability",aval).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!")).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });
    }

    private void createDatabaseAvailability(String aval) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String,Object> avalAsMap = new HashMap<>();
        avalAsMap.put("user", user.getUid());
        avalAsMap.put("availability", aval);
        db.collection("availabilities")
                .add(avalAsMap)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Availability added"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }


}
