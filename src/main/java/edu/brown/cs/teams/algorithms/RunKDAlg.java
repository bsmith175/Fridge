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

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RunKDAlg implements Command {
  @Override
  public String runCommand(String[] command, boolean dairy,
                           boolean meat, boolean nuts) throws CommandException {
    if (command.length < 3) {
      throw new CommandException("ERROR: Must enter in at least two ingredients");
    }
    try {
      Gson gson = new Gson();
      FileReader reader = new FileReader("data/ingredient_vectors.json");
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(reader);
      double[][] embeddings = new double[command.length - 1][300];
      for (int i = 1; i < command.length; i++) {
        String ingredient = REPL.removeQuotes(command[i]);
        double[] embedding = gson.fromJson(object.get(ingredient).toString(), double[].class);
        embeddings[i - 1] = embedding;
      }
      double[] queryEmbedding = Config.arrayAdd(embeddings);
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
      Gson gson = new Gson();
      FileReader reader = new FileReader("data/ingredient_vectors.json");
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(reader);
      double[][] embeddings = new double[command.length - 1][300];
      for (int i = 1; i < command.length; i++) {
        double[] embedding = gson.fromJson(object.get(command[i]).toString(), double[].class);
        embeddings[i - 1] = embedding;
      }
      double[] queryEmbedding = Config.arrayAdd(embeddings);
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
}
