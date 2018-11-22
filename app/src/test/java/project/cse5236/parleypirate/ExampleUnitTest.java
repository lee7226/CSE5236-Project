package project.cse5236.parleypirate;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetOpenAvailabilitySome() {
        MeetingAvailabilityActivity meetingAvailabilityActivity = new MeetingAvailabilityActivity();
        String[] testAvals = {"010000000000000000000000000000000000000000000010", "011000000000000000000000000000000000000000000010"};

        String result = meetingAvailabilityActivity.GetOpenAvalability(testAvals);

        assertEquals("010000000000000000000000000000000000000000000010", result);
    }
    
    @Test
    public void testGetOpenAvailabilityNone() {
        MeetingAvailabilityActivity meetingAvailabilityActivity = new MeetingAvailabilityActivity();
        String[] testAvals = {"111111111111111111111111111111111111111111111111", "000000000000000000000000000000000000000000000000"};

        String result = meetingAvailabilityActivity.GetOpenAvalability(testAvals);

        assertFalse(result.contains("1"));
    }

    @Test
    public void testGetOpenAvailabilityAll() {
        MeetingAvailabilityActivity meetingAvailabilityActivity = new MeetingAvailabilityActivity();
        String aval = "111111111111111111111111111111111111111111111111";
        String[] testAvals = {aval, aval};

        String result = meetingAvailabilityActivity.GetOpenAvalability(testAvals);

        assertEquals(aval, result);
    }

    @Test
    public void testGroupMapEmpty() {
        Group group = new Group();
        HashMap<String, Object> groupMap = group.toJson();
        HashMap<String, Object> emptyGroup = new HashMap<>();
        emptyGroup.put("members", new ArrayList<>());
        emptyGroup.put("title", null);
        assertEquals(emptyGroup, groupMap);
    }
}