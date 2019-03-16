package cse110.ucsd.team20_personalbest.ms2tests;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import cse110.ucsd.team20_personalbest.chat.ChatAdapter;
import cse110.ucsd.team20_personalbest.chat.FirebaseChatAdapter;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatMessageTest {

    FirebaseChatAdapter chatAdapter;
    CollectionReference chat;

    String COLLECTION_KEY = "testchatlogs";
    String from = "user2";
    String thisUser = "user1";
    String MESSAGE_KEY = "testmessages";

    @Before
    public void setup() {
        chatAdapter = new FirebaseChatAdapter(from, COLLECTION_KEY, thisUser+from, MESSAGE_KEY);
        chat = mock(CollectionReference.class);
        chatAdapter.mockInstantiate(chat);
    }

    @Test
    public void test_sendMessage() {

        String testMessage = "testMessage1";
        Map<String, String> testMessageMap = new HashMap<>();
        testMessageMap.put("from", from);
        testMessageMap.put("text", testMessage);

        assertNotNull(chatAdapter.getChat());

        chatAdapter.mockSendMessage(testMessageMap, null);
        verify(chat).add(testMessageMap);

        testMessage = "testMessage2";
        testMessageMap = new HashMap<>();
        testMessageMap.put("from", from);
        testMessageMap.put("text", testMessage);

        chatAdapter.mockSendMessage(testMessageMap, null);
        verify(chat).add(testMessageMap);
    }

    @Test
    public void test_correctOrder() {
        Query q = mock(Query.class);
        when(chat.orderBy(anyString(), any())).thenReturn(q);

        chatAdapter.initMessageUpdateListener(null);
        verify(chat).orderBy("timestamp", Query.Direction.ASCENDING);
    }
}
