package edu.brown.cs.teams.test;

import edu.brown.cs.teams.database.UserDatabase;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.login.AccountUser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class userDbTest {
    static UserDatabase u;
    static Connection conn;

    @BeforeClass
    public static void setUp() throws URISyntaxException, SQLException, CommandException, ClassNotFoundException {
        URI dbURI = new URI(System.getenv("DATABASE_URL"));
        String username = dbURI.getUserInfo().split(":")[0];
        String pwd = dbURI.getUserInfo().split(":")[1];

        String dbURL = "jdbc:postgresql://" + dbURI.getHost() + ':' + dbURI
                .getPort() + dbURI.getPath();
        u = new UserDatabase(dbURL, username, pwd, false);
        conn = u.getConn();
        AccountUser u1 = new AccountUser("-100", "u1", "p1");
        AccountUser u2 = new AccountUser("-101", "u2", "p2");
        AccountUser u3 = new AccountUser("-102", "u3", "p3");
        AccountUser u4 = new AccountUser("-103", "u4", "p4");
        u.addNewUser(u1);
        u.addNewUser(u2);
        u.addNewUser(u3);
        u.addNewUser(u4);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        String delete = "DELETE FROM guser WHERE uid='-100' OR uid='-101' OR uid='-102' OR uid='-103';";
        PreparedStatement prep = conn.prepareStatement(delete);
        prep.executeUpdate();
    }

    @Test
    public void favoritesTest() throws SQLException {
        //getFavorites no results
        assert (u.getFavorites("-100").size() == 0);

        //test addtoFavorites and getFavorites
        u.addToFavorites(1, "-100");
        u.addToFavorites(2, "-100");
        u.addToFavorites(3, "-100");
        List<Integer> favs = u.getFavorites("-100");
        assert (favs.contains(1));
        assert (favs.contains(2));
        assert (favs.contains(3));
        assert (favs.size() == 3);

        //removing favorite from user doesn't remove for other users
        u.addToFavorites(3, "-101");
        u.removeFavorite(3, "-101");
        favs = u.getFavorites("-100");
        assert (favs.contains(3));
        //assert remove worked
        favs = u.getFavorites("-101");
        assert (favs.size() == 0);

        //test remove
        u.removeFavorite(2, "-100");
        favs = u.getFavorites("-100");
        assert (!favs.contains(2));
        u.removeFavorite(1, "-100");
        u.removeFavorite(3, "-100");
        favs = u.getFavorites("-100");
        assert (favs.size() == 0);


    }

    @Test
    public void pantryTest() throws SQLException {
        //get with empty pantry
        assert (u.getPantry("-100").size() == 0);

        //test add to pantry
        u.addToPantry("sauce", "-100");
        u.addToPantry("chicken", "-100");
        u.addToPantry("chicken", "-101");
        List<String> pantry = u.getPantry("-100");
        assert (pantry.size() == 2);
        assert (pantry.contains("sauce"));
        assert (pantry.contains("chicken"));

        //remove pantry
        u.removePantryitem("sauce", "-100");
        u.removePantryitem("chicken", "-100");
        pantry = u.getPantry("-100");
        assert (pantry.size() == 0);

        //make sure other user's row didn't get deleted
        assert (u.getPantry("-101").contains("chicken"));
        u.removePantryitem("chicken", "-101");
        assert (u.getPantry("-101").size() == 0);

        //remove pantry item that doesn't exist
        u.removePantryitem("chicken", "-101");
        assert (u.getPantry("-101").size() == 0);

    }


}
