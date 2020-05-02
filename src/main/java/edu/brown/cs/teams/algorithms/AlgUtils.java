package edu.brown.cs.teams.algorithms;

import edu.brown.cs.teams.database.UserDatabase;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.kdtree.KDTree;
import edu.brown.cs.teams.recipe.MinimalRecipe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.Recipe;


import com.google.gson.JsonObject;

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

  /**
   * Constructor for AlgUtils.
   */
  private AlgUtils() {
  }

  /**
   * Getter method for the KDTree.
   *
   * @return the kdtree
   */
  public static KDTree<MinimalRecipe> getTree() {
    return tree;
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
   *
   * @throws SQLException for invalid queries.
   */
  public static void buildRecList() throws SQLException {
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
            + "|turkey|sausage|chicken|rib)(?!((\\s*)(chees|milk|egg)))";
  }

  /**
   * method to get dairy list string.
   *
   * @return dairy list string
   */
  public static String getDairy() {
    return "(milk|whey|cheese|yogurt|paneer|(\\s+)cream)";
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
   * Gives the most likely candidate to represent a recipe ingredient in
   * the user ingredient list.
   *
   * @param ingredients the user ingredient list
   * @param ingr        an recipe ingredient
   * @return the closest vector ingredient to the recipe ingredient
   */
  public static Ingredient generateCandidate(
          List<Ingredient> ingredients, Ingredient ingr) {
    int num = ingredients.size();
    Ingredient best = null;
    double closest = 0.0;
    for (Ingredient i : ingredients) {
      double similarity = cosineSimilarity(ingr.getVec(), i.getVec());
      if (similarity > closest) {
        best = i;
        closest = similarity;
      }
    }
    if (closest > 1 - num * SIMILARITY_FACTOR) {
      return best;
    } else {
      return null;
    }
  }


  /**
   * Method to find cosin similarity between two vectors (double arrays).
   *
   * @param vec1 the first vector
   * @param vec2 the second vector
   * @return their cosine similarity, between 0 (90 degrees) and 1 (same)
   */
  public static double cosineSimilarity(double[] vec1, double[] vec2) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < vec1.length; i++) {
      dotProduct += vec1[i] * vec2[i];
      normA += Math.pow(vec1[i], 2);
      normB += Math.pow(vec2[i], 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }


  /**
   * Method to concatenate and add vectors. Useful for Minimal Recipe which
   * does not store full ingredients.
   *
   * @param arrays the array of arrays
   * @return the vector sum of the ingredient vectors as a double array
   */
  public static double[] arrayAdd(double[][] arrays) {
    double[] result = new double[EMBED_SIZE];
    for (int i = 0; i < result.length; i++) {
      for (double[] currArr : arrays) {
        result[i] += currArr[i];
      }
    }
    for (int i = 0; i < result.length; i++) {
      result[i] /= arrays.length;
    }
    return result;
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
        double[] embedding = ingr.getVec();
        result[i] += embedding[i];
      }
      result[i] /= ingreds.size();
    }
    return result;
  }

  /**
   * Method to get the json version of a recipe (to output to gui).
   *
   * @param id the recipe id
   * @return a json that can be parsed in the front-end
   * @throws SQLException for invalid query
   */
  public static JsonObject getRecipeJson(int id) throws SQLException {
    return rdb.getRecipeContentFromID(id);
  }
}
