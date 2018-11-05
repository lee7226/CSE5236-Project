package project.cse5236.parleypirate;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class Meeting {
    private static final String USERS = "/users/";
    private static final String AVAILABILITIES = "/availabilities/";
    private Timestamp startTime;

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    private Timestamp endTime;
    private ArrayList<String> availabilities;
    private ArrayList<String> members;
    private GeoPoint location;
    private String title;

    public Meeting(){
        availabilities = new ArrayList<>();
        members = new ArrayList<>();
        location = new GeoPoint(0,0);
    }

    public void addMember(String memberId){
        members.add(USERS+memberId);
    }

    public void addAvailability(String availabilityId){
        availabilities.add(AVAILABILITIES+availabilityId);
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
