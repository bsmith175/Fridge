package edu.brown.cs.teams.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.algorithms.RecipeSuggest;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.io.CommandException;

import org.junit.Test;

import java.sql.SQLException;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SuggestTest {
  @Test
  public void testAlgorithm()
          throws CommandException, SQLException, ClassNotFoundException {
    RecipeDatabase r = new RecipeDatabase(Constants.DATABASE_FILE, false);
    new AlgUtils();
    AlgUtils.setDb(r);
    AlgUtils.buildRecList();
    RecipeSuggest recommend = new RecipeSuggest(false, false, false);
    JsonObject first = recommend.runForGui(new String[]{"chicken"}).get(0);
    String firstId = first.get("id").toString();
    assertEquals(6320, Integer.parseInt(firstId));
    first =
            recommend.runForGui(new String[]{"egg", "broccoli", "potato",
                    "cheese"}).get(0);
    firstId = first.get("id").toString();
    assertEquals(7472, Integer.parseInt(firstId));
    //testing dairy restriction
    recommend = new RecipeSuggest(false, true, false);
    first =
            recommend.runForGui(new String[]{"egg", "broccoli", "potato",
                    "cheese"}).get(0);
    firstId = first.get("id").toString();
    JsonObject meat =
            recommend.runForGui(new String[]{"egg", "broccoli", "potato",
                    "cheese"}).get(4);
    String meatId = meat.get("id").toString();
    assertEquals(5728, Integer.parseInt(firstId));
    assertEquals(9296, Integer.parseInt(meatId));
    recommend = new RecipeSuggest(true, true, true);
    meat =
            recommend.runForGui(new String[]{"egg", "broccoli", "potato",
                    "cheese"}).get(4);
    meatId = meat.get("id").toString();
    assertTrue(9296 != Integer.parseInt(meatId));
    AlgUtils.destroy();
  }
}
