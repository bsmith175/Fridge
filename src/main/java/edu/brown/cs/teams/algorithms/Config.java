package edu.brown.cs.teams.algorithms;

import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.Recipe;
import edu.brown.cs.teams.recipe.RecipeDistanceComparator;

import java.sql.SQLException;
import java.util.*;

import com.google.gson.JsonObject;

public final class Config {

  private static int embedLength = 0;
  private Map<String, double[]> recToVec = new HashMap<>();
  private static List<Recipe> fullRecipes =
          new ArrayList<>();
  private static RecipeDatabase db;

  public Config() throws SQLException {
  }

  public static void buildRecList() throws SQLException {
    fullRecipes.addAll(db.getFullRecipes("data/ingredient_vectors" +
            ".json"));
  }


  public static List<Recipe> getFullRecipes() {
    return fullRecipes;
  }


  /**
   * Gives the most likely candidate to represent a recipe ingredient in
   * the user ingredient list.
   * @param ingredients the user ingredient list
   * @param ingr an recipe ingredient
   * @return the closest vector ingredient to the recipe ingredient
   */
  public static Ingredient generateCandidate(
          List<Ingredient> ingredients, Ingredient ingr) {
    Ingredient best = null;
    double closest = 0.0;
    for (Ingredient i : ingredients) {
      double similarity = cosineSimilarity(ingr.getVec(), i.getVec());
      if (similarity > closest) {
        best = i;
        closest = similarity;
      }
    }
    if (closest > 0.9) {
      return best;
    }
    else {
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
    for (int i = 0; i < result.length; i ++){
      for (double[] currArr: arrays) {
        result[i] += currArr[i];
      }
    }
    for (int i = 0; i < result.length; i ++) {
      result[i] /= arrays.length;
    }
    return result;
  }

  public static double[] ingredAdd(Collection<Ingredient> ingreds) {
    double[] result = new double[300];
    for (int i = 0; i < result.length; i ++){
      for (Ingredient ingr : ingreds) {
        double[] embedding = ingr.getVec();
        result[i] += embedding[i];
      }
      result[i] /= ingreds.size();
    }
    return result;
  }

  public static int getEmbedLength() {
    return embedLength;
  }

  public static JsonObject getRecipeJson(int id) throws SQLException {
    return db.getRecipeContentFromID(id);
  }


  public double[] getRecVec(String id) {
    return recToVec.get(id);
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
