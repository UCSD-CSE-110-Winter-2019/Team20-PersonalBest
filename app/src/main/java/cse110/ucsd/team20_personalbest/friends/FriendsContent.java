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
    public static List<Friend> FRIENDS = new ArrayList<Friend>();

    public FriendsContent () {

    }

    public static void addFriend(String name) {
        addItem(new Friend(name));
    }

    public static void addItem(Friend item) {
        FRIENDS.add(item);
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
