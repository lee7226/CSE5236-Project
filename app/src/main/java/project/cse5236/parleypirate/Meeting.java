package project.cse5236.parleypirate;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Meeting {
    private static final String USERS = "/users/";
    private static final String AVAILABILITIES = "/availabilities/";
    private Timestamp startTime;

    private Timestamp endTime;
    private List<DocumentReference> availabilities;
    private List<DocumentReference> members;
    private GeoPoint location;
    private String title;

    private FirebaseFirestore db;

    public Meeting(){
        availabilities = new ArrayList<>();
        members = new ArrayList<>();
        location = new GeoPoint(0,0);
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void addMember(String memberId){
        db = FirebaseFirestore.getInstance();
        members.add(db.document(USERS+memberId));
    }

    public void addAvailability(String availabilityId){
        db = FirebaseFirestore.getInstance();
        availabilities.add(db.document(AVAILABILITIES+availabilityId));
    }

    public void setLocation(GeoPoint location){
        this.location = location;
    }

    public HashMap<String,Object> toJson(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("starttime",startTime);
        map.put("endtime",endTime);
        map.put("availabilities",availabilities);
        map.put("members",members);
        map.put("location",location);
        map.put("title",title);
        return map;
    }

    public void setTitle(String title){
        this.title = title;
    }

}
