package cse110.ucsd.team20_personalbest.friends;

import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Friend> FRIENDS = new ArrayList<Friend>();

    public static final Map<String, Friend> FRIENDS_MAP = new HashMap<String, Friend>();

    public static void addItem(Friend item) {
        FRIENDS.add(item);
        FRIENDS_MAP.put(item.Name, item);
    }

    private static Friend createFriend(String name) {
        return new Friend(name);
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
