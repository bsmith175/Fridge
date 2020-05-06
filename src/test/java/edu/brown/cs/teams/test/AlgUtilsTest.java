package edu.brown.cs.teams.test;

import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.recipe.Ingredient;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlgUtilsTest {

  @Test
  public void testIngredAdd()
          throws SQLException, CommandException, ClassNotFoundException {
    new AlgUtils();
    RecipeDatabase r = new RecipeDatabase(Constants.DATABASE_FILE, false);
    AlgUtils.setDb(r);
    AlgUtils.buildRecList();
    Ingredient sugar = new Ingredient("sugar");
    Ingredient apple = new Ingredient("apple");
    List<Ingredient> ingredList = new ArrayList<>();
    ingredList.add(apple);
    ingredList.add(sugar);
    double[] added = AlgUtils.ingredAdd(ingredList);
    assertEquals(added[0]*2, sugar.getPosition()[0] + apple.getPosition()[0],
            0.0001);
    assertEquals(added[30]*2,
            sugar.getPosition()[30] + apple.getPosition()[30], 0.0001);
    AlgUtils.destroy();
  }

}
