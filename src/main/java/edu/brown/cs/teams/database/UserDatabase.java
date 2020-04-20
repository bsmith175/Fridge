package edu.brown.cs.teams.database;

import edu.brown.cs.teams.login.AccountUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserDatabase {

    private Connection conn;

    /**
     * creates the user table. This table has three columns:
     *      uid (TEXT): the unique string identifying the user (primary key)
     *      name (TEXT): The user's first name. Is not unique
     *      profile (TEXT): The path to the user's profile image.
     *
     * @throws SQLException - if exception occurs while making table.
     */
    public void makeUserTable() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("CREATE TABLE user("
                + "uid TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "profile TEXT);");
        prep.executeUpdate();
    }

    /**
     * The favorite table is a junction table between recipes and users. It link every recipe
     * that is a favorite to each of the users that have it as a favorite.
     *
     * @throws SQLException
     */
    public void makeFavTable() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("CREATE TABLE favorite("
                + "recipeId TEXT FOREIGN KEY REFERENCES recipe(id),"
                + "uid TEXT FOREIGN KEY REFERENCES user(uid);");
        prep.executeUpdate();
    }

    public void addUser(AccountUser user) throws SQLException {
        String uid = user.getUid();
        String name = user.getName();
        String profilePic = user.getProfile();

        PreparedStatement prep = conn.prepareStatement("INSERT INTO user VALUES (?, ?, ?);");
        prep.setString(1, uid);
        prep.setString(2, name);
        prep.setString(3, profilePic);
        prep.addBatch();
        prep.executeUpdate();
    }


}
