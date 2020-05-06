package edu.brown.cs.teams.test;


import com.google.gson.JsonObject;
import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.algorithms.RunKDAlg;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.database.UserDatabase;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.kdtree.KDTree;
import edu.brown.cs.teams.recipe.MinimalRecipe;
import edu.brown.cs.teams.recipe.Recipe;
import org.json.simple.JSONObject;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;


public class KDTreeTest {
  public static RecipeDatabase r;
  public static UserDatabase u;
  public static KDTree<MinimalRecipe> tree;
  public static RunKDAlg alg;

  @BeforeClass
  public static void setUp() throws SQLException, CommandException, ClassNotFoundException, URISyntaxException {
    r = new RecipeDatabase(Constants.DATABASE_FILE, true);
    new AlgUtils();
    AlgUtils.setDb(r);
    AlgUtils.buildRecList();
    alg = new RunKDAlg();
    List<MinimalRecipe> recipes = AlgUtils.getMinimalRecipesList();
    tree = new KDTree<>(300);
    tree = tree.buildKDTree(recipes);
    AlgUtils.setTree(tree);
  }

  @Test
  public void testBuildTree() {
    assert(tree.isValidKDTree());
  }

  @Test
  public void testTreeSize() {
    System.out.println(tree.getSize());
    assert(tree.getSize() == 11298);
  }

  @Test
  public void testKNNSmall() throws CommandException, SQLException {
    Recipe target = r.getRecipe(2);
    MinimalRecipe minTarget = new MinimalRecipe(target.getPosition(), target.getId());
    List<MinimalRecipe> recipes = tree.naiveKnn(minTarget);
    List<MinimalRecipe> optimal = tree.getNeighbors(5, minTarget.getPosition());
    for (int i = 0; i < optimal.size(); i ++) {
      assert(optimal.get(i).getId() == recipes.get(i).getId());
    }
  }

  @Test
  public void testKNNLarge() throws CommandException, SQLException {
    Recipe target = r.getRecipe(2);
    MinimalRecipe minTarget = new MinimalRecipe(target.getPosition(), target.getId());
    List<MinimalRecipe> recipes = tree.naiveKnn(minTarget);
    List<MinimalRecipe> optimal = tree.getNeighbors(5, minTarget.getPosition());
    for (int i = 0; i < optimal.size(); i ++) {
      assert(optimal.get(i).getId() == recipes.get(i).getId());
    }
  }
  @Test
  public void testNoNN() throws CommandException, SQLException {
    Recipe target = r.getRecipe(2);
    MinimalRecipe minTarget = new MinimalRecipe(target.getPosition(), target.getId());
    List<MinimalRecipe> results = tree.getNeighbors(0, minTarget.getPosition());
    assert(results.size() == 0);
  }
// Tests for the KDAlg command
  @Test
  public void testCorrectEmbedSize() {
    String[] ingredients = new String[]{"olive oil","cannellini beans","pesto"};
    double[] embedding = alg.extractEmbedding(ingredients);
    assert(embedding.length == 300);
  }


  @AfterClass
  public static void tearDown() {
    AlgUtils.destroy();
  }

}
