package edu.brown.cs.teams.algorithms;

import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.Recipe;
import java.sql.SQLException;
import java.util.*;
import com.google.gson.JsonObject;

public final class Config {

  private static List<Recipe> fullRecipes =
          new ArrayList<>();
  private static RecipeDatabase db;

  private static String nuts =
          "(cashew|pistachio|pinyon|almond|pecan| nut|\"nut)";

  private static String meats = "(bear|beef|buffalo|bison|calf|caribou" +
          "|goat|ham|horse|kangaroo|lamb|marrow|moose" +
          "|mutton|opossum|pork|bacon|rabbit|snake|squirrrel|tripe|" +
          "turtle|veal|venison|prosciutto|cornish|duck" +
          "|goose|grouse|ostrich|partridge|pheasant|quail|squab" +
          "|turkey|sausage|chicken|rib)(?!((\\s*)(chees|milk|egg)))";

  private static String dairy = "(milk|whey|cheese|yogurt|paneer|(\\s+)cream)";

  public Config() {
  }

  public static void buildRecList() throws SQLException {
    fullRecipes.addAll(db.getFullRecipes("data/ingredient_vectors" +
            ".json"));
  }

  /**
   * method to get nuts list string
   * @return list of nuts in a string
   */
  public static String getNuts(){
    return nuts;
  }
  /**
   * method to get meat list string.
   * @return meat list string
   */
  public static String getMeats(){
    return meats;
  }
  /**
   * method to get dairy list string.
   * @return dairy list string
   */
  public static String getDairy(){
    return dairy;
  }


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
    if (closest > 1-num*0.03) {
      return best;
    } else {
      return null;
    }
  }


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


  public static double[] arrayAdd(double[][] arrays) {
    double[] result = new double[300];
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

  public static double[] ingredAdd(Collection<Ingredient> ingreds) {
    double[] result = new double[300];
    for (int i = 0; i < result.length; i++) {
      for (Ingredient ingr : ingreds) {
        double[] embedding = ingr.getVec();
        result[i] += embedding[i];
      }
      result[i] /= ingreds.size();
    }
    return result;
  }

  public static double euclidDistance(double x[], double y[]) {
    double ds = 0.0;
    for(int n = 0; n < x.length; n++)
      ds += Math.pow(x[n] - y[n], 2.0);
    ds = Math.sqrt(ds);
    return  ds;
  }

  public static JsonObject getRecipeJson(int id) throws SQLException {
    return db.getRecipeContentFromID(id);
  }


  public static void setDb(RecipeDatabase newDB) {
    db = newDB;
  }

  public static void printRecIngreds(Recipe r) {
    System.out.println();
    System.out.println(r.getId() + "   ->     ");
    for (Ingredient i : r.getIngredients()) {
      System.out.print(i.getId() + " ");
    }
  }

}
