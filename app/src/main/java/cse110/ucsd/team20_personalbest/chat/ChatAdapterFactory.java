package cse110.ucsd.team20_personalbest.chat;

import android.util.Log;

import static android.content.ContentValues.TAG;

public class ChatAdapterFactory {


    public static ChatAdapter build(String type, String from, String ck, String dk, String mk){
        if(type.equals("mock")){
            ChatAdapter mb = new MockChatAdapter(from, ck, dk, mk);
            mb.instantiate();
            Log.d(TAG, "Mock Chat Adapter created");
            return mb;
        }
        else{
            ChatAdapter fb = new FirebaseChatAdapter(from, ck, dk, mk);
            fb.instantiate();
            Log.d(TAG, "Normal Chat Adapter created");
            return fb;
        }
    }
}
