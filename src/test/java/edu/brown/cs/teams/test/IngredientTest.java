
package edu.brown.cs.teams.test;

import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.Recipe;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IngredientTest {

  @Test
  public void testGenerateCandidate()
          throws SQLException, CommandException, ClassNotFoundException {
    RecipeDatabase r = new RecipeDatabase(Constants.DATABASE_FILE, false);
    new AlgUtils();
    AlgUtils.setDb(r);
    AlgUtils.buildRecList();
    Ingredient sugar = new Ingredient("sugar");
    Ingredient apple = new Ingredient("apple");
    Ingredient apples = new Ingredient("apples");
    Ingredient chicken = new Ingredient("chicken");
    Ingredient whisky = new Ingredient("whisky");
    List<Ingredient> ingredList = new ArrayList<>();
    ingredList.add(apple);
    ingredList.add(sugar);
    ingredList.add(chicken);
    System.out.println(apple.getDistance(apples.getPosition()));
    assertEquals(apple, apples.generateCandidate(ingredList));
    assertNull(whisky.generateCandidate(ingredList));
    AlgUtils.destroy();
  }
}
