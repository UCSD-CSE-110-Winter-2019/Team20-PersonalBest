package cse110.ucsd.team20_personalbest.friends;

import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<Friend> FRIENDS = new ArrayList<Friend>();

    public static List<String> FRIENDS_TOBEADDED = new ArrayList<String>();

    public FriendsContent () {

    }

    public static void addFriend(String name) {
        addItem(new Friend(name));
    }

    public static void addItem(Friend item) {
        for(int i = 0; i < FRIENDS.size(); i++) {
            if(FRIENDS.get(i).Name.equals(item.Name))
                return;
        }
        FRIENDS.add(item);
        Log.d("FriendsContent","Friend added");
    }

    public static class Friend {
        public final String Name;

        public Friend(String Name) {
            this.Name = Name;
        }

        @Override
        public String toString() {
            return Name;
        }
    }
}
