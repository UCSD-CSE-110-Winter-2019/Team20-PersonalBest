package cse110.ucsd.team20_personalbest.friends;

public interface FirebaseCommandCenterInterface {

    void updateFriends();
    void addFriend(String friendEmail);
    boolean userExists(String userToLookup);
}
