package edu.brown.cs.teams.algorithms;

import edu.brown.cs.teams.database.UserDatabase;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.kdtree.KDTree;
import edu.brown.cs.teams.recipe.MinimalRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.Recipe;

/**
 * Class to store the information relevant to the algorithms being run. Also
 * has convenient util static methods.
 */
public final class AlgUtils {
  private static RecipeDatabase rdb;
  private static UserDatabase udb;
  private static List<Recipe> fullRecipes =
          new ArrayList<>();
  public static final int EMBED_SIZE = 300;
  public static final double SIMILARITY_FACTOR = 0.03;
  private static KDTree<MinimalRecipe> tree = new KDTree<>(EMBED_SIZE);
  private static Map<String, double[]> vectorMap = new HashMap<>();
  public static final double SIMILARITY_THRESHOLD = 0.75;

  /**
   * Constructor for AlgUtils.
   */
  public AlgUtils() {
  }

  /**
   * Getter method for the KDTree.
   *
   * @return the kdtree
   */
  public static KDTree<MinimalRecipe> getTree() {
    return tree;
  }

  public static Map<String, double[]> getVectorMap() {
    return vectorMap;
  }

  /**
   * Getter method for the db proxy.
   *
   * @return the db
   */
  public static RecipeDatabase getRecipeDb() {
    return rdb;
  }

  /**
   * Getter method for the db proxy.
   *
   * @return the db
   */
  public static UserDatabase getUserDb() {
    return udb;
  }

  /**
   * Setter method for the kd tree.
   *
   * @param tree the kdtree to set to
   */
  public static void setTree(KDTree<MinimalRecipe> tree) {
    AlgUtils.tree = tree;
  }

  /**
   * getter method for the minimal recipe list.
   *
   * @return list of minimal recipes
   */
  public static List<MinimalRecipe> getMinimalRecipesList() {
    List<MinimalRecipe> listMin = new ArrayList<>();
    for (Recipe rec : fullRecipes) {
      MinimalRecipe minRec = new MinimalRecipe(rec.getPosition(), rec.getId());
      listMin.add(minRec);
    }
    return listMin;
  }

  /**
   * setter method for the databases.
   *
   * @param recipe - recipe database
   * @param user   - user database
   */
  public static void setDb(RecipeDatabase recipe, UserDatabase user) {
    rdb = recipe;
    udb = user;
  }

  /**
   * builds the full recipe list from the database and stores it in Config
   * class.
   */
  public static void buildRecList() throws CommandException {
    String filename = "data/ingredient_vectors.json";
    fullRecipes.addAll(rdb.getFullRecipes(filename));
  }

  /**
   * method to get nuts list string.
   *
   * @return list of nuts in a string
   */
  public static String getNuts() {
    //filter regex for dairy, meat, nuts
    return "(cashew|pistachio|pinyon|almond|pecan| nut|\"nut)";
  }

  /**
   * method to get meat list string.
   *
   * @return meat list string
   */
  public static String getMeats() {
    return "(bear|beef|buffalo|bison|calf|caribou"
            + "|goat|ham|horse|kangaroo|lamb|marrow|moose"
            + "|mutton|opossum|pork|bacon|rabbit|snake|squirrrel|tripe|"
            + "turtle|veal|venison|prosciutto|cornish|duck"
            + "|goose|grouse|ostrich|partridge|pheasant|quail|squab"
            + "|turkey|sausage|chicken|rib|goat's|goats)(?!(('s)?(\\s*)"
            + "(chees|milk|egg)))";
  }

  /**
   * method to get dairy list string.
   *
   * @return dairy list string
   */
  public static String getDairy() {
    return "(milk|whey|cheese|yogurt|paneer|butter|cheddar|mascarpone|gouda"
            + "|mozzarella|parmesan|(\\s+)cream)";
  }

  public static void destroy() {
    rdb = null;
    tree = null;
    fullRecipes = new ArrayList<>();
  }


  /**
   * Method to get the full recipe list.
   *
   * @return the list of recipes in the database.
   */
  public static List<Recipe> getFullRecipes() {
    return fullRecipes;
  }


  /**
   * Method to concatenate and add ingredient vectors in a collection of
   * ingredients.
   *
   * @param ingreds the ingredient collection
   * @return the vector sum of the ingredient vectors as a double array
   */
  public static double[] ingredAdd(Collection<Ingredient> ingreds) {
    double[] result = new double[EMBED_SIZE];
    for (int i = 0; i < result.length; i++) {
      for (Ingredient ingr : ingreds) {
        double[] embedding = ingr.getPosition();
        result[i] += embedding[i];
      }
      result[i] /= ingreds.size();
    }
    return result;
  }

  /**
   * method do ONLY set the recipe database. Useful for testing without
   * needing to access all the user information.
   *
   * @param r the recipe database
   */
  public static void setDb(RecipeDatabase r) {
    rdb = r;
  }

}
