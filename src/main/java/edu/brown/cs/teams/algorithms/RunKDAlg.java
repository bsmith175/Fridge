package edu.brown.cs.teams.algorithms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.database.RecipeDatabase;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import edu.brown.cs.teams.io.Command;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.io.REPL;
import edu.brown.cs.teams.recipe.MinimalRecipe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RunKDAlg implements Command {

  /**
   * Helper method to get the resulting query embedding for a list of ingredients/
   * @param command
   * @param forGui
   * @return a double[] for the result
   * @throws CommandException
   * @throws IOException
   * @throws ParseException
   */
  public static double[] extractQuery(String[] command, boolean forGui) throws CommandException, IOException, ParseException {
    double[][] embeddings;
    if (!forGui) {
      embeddings = new double[command.length - 1][300];
    } else {
      embeddings = new double[command.length][300];
    }

    Gson gson = new Gson();
    FileReader reader = new FileReader("data/ingredient_vectors.json");
    JSONParser parser = new JSONParser();
    JSONObject object = (JSONObject) parser.parse(reader);
    int start = 1;
    if (forGui) {
      start = 0;
    }
    for (int i = start; i < embeddings.length; i++) {
      String ingredient = command[i];
      if (!forGui){
        ingredient = REPL.removeQuotes(ingredient);
      }
      double[] embedding = gson.fromJson(object.get(ingredient).toString(), double[].class);
      if (forGui) {
        embeddings[i] = embedding;
      } else {
        embeddings[i-1] = embedding;
      }
    }
    double[] queryEmbedding = Config.arrayAdd(embeddings);
    return queryEmbedding;
  }

  @Override
  public String runCommand(String[] command, boolean dairy,
                           boolean meat, boolean nuts) throws CommandException {
    if (command.length < 3) {
      throw new CommandException("ERROR: Must enter in at least two ingredients");
    }
    try {
      double[] queryEmbedding = extractQuery(command, false);
      List<MinimalRecipe> neighbors = AlgMain.getTree().getNeighbors(100, queryEmbedding);
      String result = "";
      for (MinimalRecipe recipe : neighbors) {
        result += AlgMain.getRecipeDb().getRecipe(recipe.getId());
      }
      return result;
    } catch (IOException | ParseException e) {
      throw new CommandException(e.getMessage());
    }
  }

  @Override
  public List<JsonObject> runForGui(String[] command, boolean dairy,
                                    boolean meat, boolean nuts) throws CommandException {
    try {
      double[] queryEmbedding = extractQuery(command, true);
      List<MinimalRecipe> neighbors = AlgMain.getTree().getNeighbors(100, queryEmbedding);
      List<JsonObject> results = new ArrayList<>();
      RecipeDatabase db = AlgMain.getRecipeDb();
      for (MinimalRecipe recipe : neighbors) {
        results.add(db.getRecipeContentFromID(recipe.getId()));
      }
      return results;
    } catch (IOException | ParseException | SQLException e) {
      throw new CommandException(e.getMessage());
    }
  }


  public static List<JsonObject> getRecommendations(String uid) throws CommandException {
    try {
      List<Integer> favoriteIds = AlgMain.getUserDb().getFavorites(uid);
      Set<Integer> resultIds = new LinkedHashSet<>();
      for (Integer i : favoriteIds) {
        String[] tokens = AlgMain.getRecipeDb().getTokenIngredients(i);
        double[] embeddings = extractQuery(tokens, true);
        List<MinimalRecipe> result = AlgMain.getTree().getNeighbors(1, embeddings);
        for (MinimalRecipe recipe : result) {
          resultIds.add(recipe.getId());
        }
      }
      List<JsonObject> recipes = new ArrayList<>();
      RecipeDatabase db = AlgMain.getRecipeDb();
      for (Integer id: resultIds) {
        if (!favoriteIds.contains(id)) {
          recipes.add(db.getRecipeContentFromID(id));
        }
      }
      Collections.shuffle(recipes);
      return recipes;
    } catch (SQLException | IOException | ParseException e) {
      throw new CommandException(e.getMessage());
    }
  }

}
