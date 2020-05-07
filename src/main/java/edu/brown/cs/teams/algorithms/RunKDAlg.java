package edu.brown.cs.teams.algorithms;

import com.google.gson.JsonObject;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.recipe.Ingredient;
import org.json.simple.parser.ParseException;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.recipe.MinimalRecipe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RunKDAlg {


  /**
   * Helper method to get the resulting query embedding for a list of ingredients.
   * @param ingredients the resulting embedding
   * @return a double[] for the result
   * @throws CommandException
   * @throws IOException
   * @throws ParseException
   */
  public static double[] extractEmbedding(String[] ingredients) {
    List<Ingredient> ingredientSet = new ArrayList<>();
    for (int i = 0; i < ingredients.length; i++) {
      Ingredient ing = new Ingredient(ingredients[i]);
      ingredientSet.add(ing);
    }
    double[] queryEmbedding = AlgUtils.ingredAdd(ingredientSet);
    return queryEmbedding;
  }

  public List<JsonObject> getRecommendations(String uid) throws CommandException {
    try {
      List<Integer> favoriteIds = AlgUtils.getUserDb().getFavorites(uid);
      Set<Integer> resultIds = new LinkedHashSet<>();
      for (Integer i : favoriteIds) {
        String[] tokens = AlgUtils.getRecipeDb().getTokenIngredients(i);
        double[] embeddings = extractEmbedding(tokens);
        List<MinimalRecipe> result = AlgUtils
            .getTree().getNeighbors(5, embeddings);
        for (MinimalRecipe recipe : result) {
          resultIds.add(recipe.getId());
        }
      }
      List<JsonObject> recipes = new ArrayList<>();
      RecipeDatabase db = AlgUtils.getRecipeDb();
      for (Integer id: resultIds) {
        if (!favoriteIds.contains(id)) {
          recipes.add(db.getRecipeContentFromID(id));
        }
      }
      Collections.shuffle(recipes);
      return recipes;
    } catch (SQLException e) {
      throw new CommandException(e.getMessage());
    }
  }

}
