package project.cse5236.parleypirate;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Availability {

    private static final String TAG = "Availability";

    private String userId;
    private static String availability = "why";

    public String[] weekDays = {"Monday"};

    public String GetOpenAvalability(String[] avals) {
        String openAval = "";
        int[] intRepresentations = new int[avals.length];

        // convert all given avals to int then to binary
        for (int i = 0; i < avals.length; i++) {
            intRepresentations[i] = Integer.parseInt(avals[i], 2);
        }

        int bitwisedAvals = 0;

        // do bitwise or on binary avals
        for (int intRepresentation : intRepresentations) {
            bitwisedAvals = bitwisedAvals | intRepresentation;
        }
        // set openAvals to the result
        openAval = Integer.toString(bitwisedAvals, 2);

        return  openAval;
    }

    public void StoreAvailability(String aval) {
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

    public String GetAvailability(String userId) {
        availability = "isn't this working";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference avalRef = db.collection("availabilities");
        final Query query = avalRef.whereEqualTo("user",userId);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot qs = task.getResult();
                if (qs.size()>0) {
                    List<DocumentSnapshot> avalList = qs.getDocuments();
                    availability = (String)avalList.get(0).get("availability");
                    Log.d(TAG, "DocumentSnapshot data: " + qs.getDocuments());
                } else {
                    availability = "";
                    Log.d(TAG, "user has no availability");
                }
            }else{
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        return availability;
    }
}
