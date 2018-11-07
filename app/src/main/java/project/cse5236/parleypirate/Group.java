package project.cse5236.parleypirate;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Group {
    private static final String USERS = "/users/";
    private static final String AVAILABILITIES = "/availabilities/";

    private List<DocumentReference> members;
    private String title;

    private FirebaseFirestore db;

    public Group(){
        members = new ArrayList<>();
    }

    public void addMember(String memberId){
        db = FirebaseFirestore.getInstance();
        members.add(db.document(USERS+memberId));
    }


    public HashMap<String,Object> toJson(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("members",members);
        map.put("title",title);
        return map;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
