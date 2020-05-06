package edu.brown.cs.teams.login;

/**
 * Class represents a User account.
 *
 * @author bensmith
 */
public class AccountUser {
  static final String DEFAULT_PICTURE = "path/to/image";

  //the user's unique ID
  private String uid;

  //user's given name
  private String name;

  //path to user's profile picture
  private String profile;

  public AccountUser(String id, String firstName, String pfp) {
    uid = id;
    name = firstName;
    profile = pfp;
    //list of recipe IDs that user has favorited
    //Idk if we actually need this, since we have to query the db anyway
  }

  /**
   * Getter for uid.
   *
   * @return - the User's user ID
   */
  public String getUid() {
    return uid;
  }

  /**
   * Sets the user ID.
   *
   * @param uid - ID to set
   */
  public void setUid(String uid) {
    this.uid = uid;
  }

  /**
   * Gets user's first name.
   *
   * @return - The user's first name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the user's name.
   *
   * @param name - User's first name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the path to the user's profile picture.
   *
   * @return - Path to user's profile pic
   * Default profile picture if user has no profile picture
   */
  public String getProfile() {
    if (profile == null) {
      return DEFAULT_PICTURE;
    }
    return profile;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj.getClass() != getClass()) {
      return false;
    }

    return ((AccountUser) obj).uid.equals(uid);
  }

  @Override
  public int hashCode() {
    final int hash = 17;
    final int hashMultiplier = 31;
    int ret = hash;
    ret = hashMultiplier * ret * uid.hashCode();
    return ret;
  }


}
