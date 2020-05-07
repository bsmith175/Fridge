//package edu.brown.cs.teams.test;
//
//import edu.brown.cs.teams.algorithms.AlgUtils;
//import edu.brown.cs.teams.constants.Constants;
//import edu.brown.cs.teams.database.RecipeDatabase;
//import edu.brown.cs.teams.io.CommandException;
//import edu.brown.cs.teams.recipe.Ingredient;
//import edu.brown.cs.teams.recipe.Recipe;
//import org.junit.Test;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.Assert.*;
//
//public class RecipeTest {
//
//
//  @Test
//  public void testGenerateCandidate()
//          throws CommandException, SQLException, ClassNotFoundException {
//    RecipeDatabase r = new RecipeDatabase(Constants.DATABASE_FILE, false);
//    new AlgUtils();
//    AlgUtils.setDb(r);
//    AlgUtils.buildRecList();
//    Ingredient apple = new Ingredient("apple");
//    Ingredient apples = new Ingredient("apples");
//    Ingredient whisky = new Ingredient("whisky");
//    Ingredient bourbon = new Ingredient("bourbon");
//    Set<Ingredient> ingredList = new HashSet<>();
//    ingredList.add(apple);
//    List<Ingredient> myIngreds = new ArrayList<>();
//    myIngreds.add(apples);
//    Recipe recipe = new Recipe(new double[]{}, 1000000, ingredList);
//    assertTrue(recipe.compareToIngredients(myIngreds).contains(apples));
//    assertEquals(1, recipe.compareToIngredients(myIngreds).size());
//    ingredList.add(whisky);
//    assertTrue(recipe.compareToIngredients(myIngreds).contains(apples));
//    assertEquals(1, recipe.compareToIngredients(myIngreds).size());
//    myIngreds.add(bourbon);
//    assertTrue(recipe.compareToIngredients(myIngreds).contains(apples));
//    assertTrue(recipe.compareToIngredients(myIngreds).contains(bourbon));
//    assertEquals(2, recipe.compareToIngredients(myIngreds).size());
//    ingredList.remove(whisky);
//    assertTrue(recipe.compareToIngredients(myIngreds).contains(apples));
//    assertEquals(1, recipe.compareToIngredients(myIngreds).size());
//    AlgUtils.destroy();
//  }
//}
