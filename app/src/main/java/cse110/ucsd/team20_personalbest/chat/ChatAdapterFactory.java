package cse110.ucsd.team20_personalbest.chat;

public class ChatAdapterFactory {


    public static ChatAdapter build(String type, String from, String ck, String dk, String mk){
        if(type.equals("mock")){
            ChatAdapter mb = new MockChatAdapter(from, ck, dk, mk);
            mb.instantiate();
            return mb;
        }
        else{
            ChatAdapter fb = new FirebaseChatAdapter(from, ck, dk, mk);
            fb.instantiate();
            return fb;
        }
    }
}
