package edu.brown.cs.teams.database;

import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.login.AccountUser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains functionality for the PostgreSQL user database.
 */
public class UserDatabase {
  private Connection conn;


  /**
   * Constructs a postgresql RecipeDatabase. Connects to database server.
   * @param url a String
   * @param user a String
   * @param pwd a String
   * @param init a Boolean
   */
  public UserDatabase(String url, String user, String pwd, Boolean init)
      throws ClassNotFoundException,
      SQLException, CommandException {
    Class.forName("org.postgresql.Driver");
    conn = DriverManager.getConnection(url, user, pwd);
    if (!init) {
      try {
        verifyTables();
      } catch (SQLException e) {
        throw new CommandException("ERROR: PostgreSQL database is malformed");
      }
    }
  }


  //-------------------------- User tables setup --------------------------------

  /**
   * Initialized the user database. Creates all necessary tables.
   * @throws SQLException - if error occurs while creating tables
   */
  public void initUserDB() throws SQLException {
    makeUserTable();
    makeExcludeTable();
    makeFavTable();
    makePantryTable();
    verifyTables();
  }


//
//    Dummy method to verify all the columns in the recipe table are there.
//
//    @throws SQLException
//
  private void verifyTables() throws SQLException {
    String query =
            "SELECT guser.uid, guser.name, guser.profile, favorite.recipeid, favorite.uid,"
                + " exclude.category, exclude.uid"
                    + " FROM guser, favorite, exclude "
                    + "LIMIT 1;";

    PreparedStatement prep = conn.prepareStatement(query);
    prep.executeQuery();
  }


//    creates the user table. This table has three columns:
//    uid (TEXT): the unique string identifying the user (primary key)
//    name (TEXT): The user's first name. Is not unique
//    profile (TEXT): The path to the user's profile image.

  private void makeUserTable() throws SQLException {
    PreparedStatement prep = conn.prepareStatement("CREATE TABLE guser("
            + "uid TEXT PRIMARY KEY, "
            + "name TEXT, "
            + "profile TEXT);");
    prep.executeUpdate();
  }

//    The favorite table is a junction table between recipes and users. It links every recipe
//    that is a favorite to each of the users that have it as a favorite.
  private void makeFavTable() throws SQLException {
    PreparedStatement prep = conn.prepareStatement("CREATE TABLE favorite("
            + "recipeId INTEGER, "
            + "uid TEXT REFERENCES guser(uid));");
    prep.executeUpdate();
  }


  //The exclude table links every user to each food category that they chose to exclude.
  //This feature is not currently implemented
  private void makeExcludeTable() throws SQLException {
    PreparedStatement prep = conn.prepareStatement("CREATE TABLE exclude("
            + "category TEXT, "
            + "uid TEXT REFERENCES guser(uid));");
    prep.executeUpdate();
  }


//    The favorite table is a junction table between recipes and users. It links every recipe
//    that is a favorite to each of the users that have it as a favorite.

  private void makePantryTable() throws SQLException {
    PreparedStatement prep = conn.prepareStatement("CREATE TABLE pantry("
            + "ingredient TEXT, "
            + "uid TEXT REFERENCES guser(uid));");
    prep.executeUpdate();
  }

  //-------------------------- User tables methods --------------------------------

  /**
   * Adds a new user to the database.
   *
   * @param user - the User to be added to the database
   * @throws SQLException - if user could not be added
   *                      - if user already exists
   */
  public void addNewUser(AccountUser user) throws SQLException {
    String uid = user.getUid();
    String name = user.getName();
    String profilePic = user.getProfile();

    PreparedStatement prep = conn.prepareStatement("INSERT INTO guser VALUES (?, ?, ?);");
    prep.setString(1, uid);
    prep.setString(2, name);
    prep.setString(3, profilePic);
    prep.addBatch();
    prep.executeUpdate();
  }


  /**
   * Queries a user's favorite recipe list, given a user ID.
   *
   * @param uid - User ID
   * @return - A List of recipe IDs
   * @throws SQLException - if exception occurs during query
   */
  public List<Integer> getFavorites(String uid) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT recipeId FROM favorite WHERE uid= ?");
    prep.setString(1, uid);
    ResultSet res = prep.executeQuery();
    List<Integer> ret = new ArrayList<Integer>();
    while (res.next()) {
      ret.add(res.getInt(1));
    }
    return ret;
  }


  /**
   * Attemps to add a recipe to the user's favorites list.
   *
   * @param rid - ID of recipe
   * @param uid - ID of user
   * @return True
   * -if recipe was successfully added to favorites list
   * False
   * - if recipe was already in user's favorites list
   * @throws SQLException - if exception occurs while updating database
   */
  public Boolean addToFavorites(int rid, String uid) throws SQLException {
    String check = "SELECT EXISTS(SELECT * FROM favorite WHERE recipeId= ?  AND uid= ?);";
    PreparedStatement prep = conn.prepareStatement(check);
    prep.setInt(1, rid);
    prep.setString(2, uid);
    ResultSet rs = prep.executeQuery();
    rs.next();

    if (rs.getBoolean(1)) {
      return false;
    }
    prep = conn.prepareStatement("INSERT INTO favorite VALUES(?, ?)");
    prep.setInt(1, rid);
    prep.setString(2, uid);
    prep.addBatch();
    prep.executeUpdate();
    return true;
  }

  /**
   * Attemps to remove a recipe to the user's favorites list.
   *
   * @param rid - ID of recipe
   * @param uid - ID of user
   * @throws SQLException - if exception occurs while updating database
   */
  public void removeFavorite(int rid, String uid) throws SQLException {
    PreparedStatement prep =
        conn.prepareStatement("DELETE FROM favorite WHERE uid=? AND recipeid=?");
    prep.setString(1, uid);
    prep.setInt(2, rid);
    prep.executeUpdate();
  }

  /**
   * Attemps to add a recipe to the user's favorites list.
   *
   * @param ingredient - string of ingredients to add
   * @param uid         - ID of user
   * -if recipe was successfully added to favorites list
   * False
   * - if recipe was already in user's favorites list
   * @throws SQLException - if exception occurs while updating database
   */
  public void addToPantry(String ingredient, String uid) throws SQLException {
    String check = "SELECT EXISTS(SELECT * FROM pantry WHERE ingredient= ?  AND uid= ?);";
    PreparedStatement prep = conn.prepareStatement(check);
    prep.setString(1, ingredient.trim());
    prep.setString(2, uid);
    ResultSet rs = prep.executeQuery();
    rs.next();

    if (!rs.getBoolean(1)) {
      prep = conn.prepareStatement("INSERT INTO pantry VALUES(?, ?)");
      prep.setString(1, ingredient);
      prep.setString(2, uid);
      prep.addBatch();
      prep.executeUpdate();
    }
  }

  /**
   * Attemps to remove a recipe to the user's favorites list.
   *
   * @param ingredient - string of ingredients to remove
   * @param uid        - ID of user
   * @throws SQLException - if exception occurs while updating database
   */
  public void removePantryitem(String ingredient, String uid) throws SQLException {
    PreparedStatement prep =
        conn.prepareStatement("DELETE FROM pantry WHERE uid=? AND ingredient=?");
    prep.setString(1, uid);
    prep.setString(2, ingredient.trim());
    prep.executeUpdate();
  }

  /**
   * Queries a user's pantry ingredients, given a user ID.
   *
   * @param uid - User ID
   * @return - A List of pantry ingredients
   * @throws SQLException - if exception occurs during query
   */
  public List<String> getPantry(String uid) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT ingredient FROM pantry WHERE uid= ?");
    prep.setString(1, uid);
    ResultSet res = prep.executeQuery();
    List<String> ret = new ArrayList<>();
    while (res.next()) {
      ret.add(res.getString(1));
    }
    return ret;
  }

  public Connection getConn() {
    return conn;
  }
}
