package edu.brown.cs.teams.login;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a User account
 *
 * @author bensmith
 */
public class AccountUser implements User {
    static final String DEFAULT_PICTURE = "path/to/image";

    //the user's unique ID
    private String uid;

    //user's given name
    private String name;

    //path to user's profile picture
    private String profile;

    //list of recipe IDs that user has favorited
    //Idk if we actually need this, since we have to query the db anyway
    private List<Integer> favorites;

    public AccountUser(String id, String firstName, String pfp) {
        uid = id;
        name = firstName;
        profile = pfp;
        favorites = new ArrayList<>();
    }
    /**
     * Getter for uid.
     * @return - the User's user ID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the user ID.
     * @param uid - ID to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets user's first name.
     * @return - The user's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     * @param name - User's first name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the path to the user's profile picture.
     * @return - Path to user's profile pic
     *           Default profile picture if user has no profile picture
     */
    public String getProfile() {
        if (profile == null) {
            return DEFAULT_PICTURE;
        }
        return profile;
    }

    /**
     * Sets the user's profile pic path.
     * @param profile - Path to User's profile picture
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Gets the user's favorite list.
     * @return - The user's list of favorite recipe IDs,
     *           null if no favorites list has been set
     */
    public List<Integer> getFavorites() {
        return favorites;
    }

    /**
     * Sets the user's favorite list.
     * @param favs - A list of recipe IDs to set as favorites list
     */
    public void setFavorites(List<Integer> favs) {
        favorites = favs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        if (((AccountUser) obj).uid.equals(uid)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int HASH = 17;
        final int HASH_MULTIPLIER = 31;

        int ret = HASH;
        ret = HASH_MULTIPLIER * ret * uid.hashCode();
        return ret;
    }


}
