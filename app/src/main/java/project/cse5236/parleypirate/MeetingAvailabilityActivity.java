package project.cse5236.parleypirate;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.android.gms.common.util.ArrayUtils;

public class MeetingAvailabilityActivity extends AppCompatActivity {

    private static final String TAG = "MeetingAvalActivity";

    private Spinner mAvailabilitySpinner;
    private Button[] mAvailabilityButtons;
    private Button mSaveAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String[] test = new String[]{};
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        List<String> userIds = new ArrayList<>();
        List<String> l = new ArrayList<>();
        if (extras != null && extras.size() > 0) {
            for (int i = 0; i < extras.size(); i++) {
                userIds.add(extras.getString("user" + i));
            }
        }

        mAvailabilityButtons = new Button[48];

        for (int i = 0; i < mAvailabilityButtons.length; i++) {
            int resourceId = this.getResources().getIdentifier("button_availability" + i, "id", this.getPackageName());
            mAvailabilityButtons[i] = findViewById(resourceId);
            mAvailabilityButtons[i].setBackgroundColor(getResources().getColor(R.color.colorRed));
        }

        DisplayOpenAvals(userIds.toArray(new String[userIds.size()]));
        //String open = GetOpenAvalability(userIds.toArray(new String[userIds.size()]));

//        // TODO: this needs to be updated to not do the same thing as AvailabilityActivity
//        CollectionReference avalRef = db.collection("availabilities");
//        final Query query = avalRef.whereEqualTo("user",user.getUid());
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @SuppressLint("LogNotTimber")
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    QuerySnapshot qs = task.getResult();
//                    if (qs.size()>0) {
//                        List<DocumentSnapshot> avalList = qs.getDocuments();
//                        String availabilityInnerString = (String)avalList.get(0).get("availability");
//                        for (int i = 0; i < mAvailabilityButtons.length; i++) {
//                            if (availabilityInnerString.charAt(i) == '1') {
//                                mAvailabilityButtons[i].setBackgroundColor(getResources().getColor(R.color.colorGreen));
//                                mAvailabilityButtons[i].setContentDescription("1");
//                            }
//                        }
//                        Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
//                    } else {
//                        Log.d(TAG, "user has no availability");
//                    }
//                }else{
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

        mSaveAvailability = findViewById(R.id.button_save_availability);
        mSaveAvailability.setVisibility(View.GONE);


        mAvailabilitySpinner = findViewById(R.id.spinnerDayOfWeek);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAvailabilitySpinner.setAdapter(adapter);
    }

    public void DisplayOpenAvals(String[] avals) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        CollectionReference avalRef = db.collection("availabilities");
        //final Query query = avalRef.get()//avalRef.whereEqualTo("availability", "aval");
        //query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        avalRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot qs = task.getResult();
                    if (qs.size()>0) {
                        List<DocumentSnapshot> avalList = qs.getDocuments();
                        List<String> avalStrList = new ArrayList<>();
                        for (int i = 0; i < avalList.size(); i++) {
                            if (ArrayUtils.contains(avals, avalList.get(i).get("user"))) {
                                String availabilityInnerString = (String)avalList.get(i).get("availability");
                                avalStrList.add(availabilityInnerString);
                            }
                        }

                        String openAvals = GetOpenAvalability(avalStrList.toArray(new String[avalStrList.size()]));
                        for (int i = 0; i < mAvailabilityButtons.length && i < openAvals.length(); i++) {
                            if (openAvals.charAt(i) == '1') {
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
            }
        });
    }

    public String GetOpenAvalability (String[] avals) {
        String openAval = "";
        long[] intRepresentations = new long[avals.length];

        // convert all given avals to int then to binary
        for (int i = 0; i < avals.length; i++) {
            intRepresentations[i] = Long.parseLong(avals[i], 2);
        }

        long bitwisedAvals = 0;

        // do bitwise and on binary avals
        for (int i = 0; i < intRepresentations.length; i++) {
            if (i == 0) {
                bitwisedAvals = bitwisedAvals | intRepresentations[i];
            } else {
                bitwisedAvals = bitwisedAvals & intRepresentations[i];
            }
        }
        // set openAvals to the result
        openAval = Long.toString(bitwisedAvals, 2);

        return  openAval;
    }
}
