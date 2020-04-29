package edu.brown.cs.teams.algorithms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import edu.brown.cs.teams.io.Command;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.Recipe;
import edu.brown.cs.teams.recipe.RecipeDistanceComparator;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class RunSuperiorAlg implements Command {
  @Override
  public String runCommand(String[] command, boolean dairy,
                           boolean meat, boolean nuts) throws CommandException {
    if (command.length < 2) {
      throw new CommandException("ERROR: Must enter an ingredient");
    }
    try {
      PriorityQueue<Recipe> recpq = preCommand(command);
      Recipe first = recpq.poll();
      Config.printRecIngreds(first);
      first = recpq.poll();
      Config.printRecIngreds(first);
      first = recpq.poll();
      Config.printRecIngreds(first);
      first = recpq.poll();
      Config.printRecIngreds(first);
      return Integer.toString(first.getId());
    } catch (IOException | ParseException e) {
      throw new CommandException(e.getMessage());
    }
  }

  private PriorityQueue<Recipe> preCommand(String[] command) throws IOException,
          ParseException {
    Gson gson = new Gson();
    FileReader reader = new FileReader("data/ingredient_vectors.json");
    JSONParser parser = new JSONParser();
    JSONObject object = (JSONObject) parser.parse(reader);
    List<Ingredient> ingredients = new ArrayList<>();
    for (String word : Arrays.copyOfRange(command, 1, command.length)) {
      word = word.replaceAll("\"", "");
      try {
        double[] embedding = gson.fromJson(object.get(word).toString(),
                double[].class);

        Ingredient ingredient = new Ingredient(word, embedding);
        ingredients.add(ingredient);
      } catch (NullPointerException e) {
        System.out.println(word + " is not a valid ingredient in our " +
                "database. It will be ignored");
      }
    }

    for (Recipe recipe : Config.getFullRecipes()) {
      recipe.compareToIngredients(ingredients);
    }
    List<Recipe> reclist = Config.getFullRecipes();
    PriorityQueue<Recipe> recpq =
            new PriorityQueue<>(new RecipeDistanceComparator());

    recpq.addAll(reclist);
    return recpq;
  }

  @Override
  public List<JsonObject> runForGui(String[] command, boolean dairy,
                                    boolean meat, boolean nuts) throws CommandException {
    if (command.length < 2) {
      throw new CommandException("ERROR: Must enter an ingredient");
    }
    StringBuilder notAllowed = new StringBuilder();
    if (dairy = true) {
      notAllowed.append(Config.getDairy());
      notAllowed.append("|");
    }
    if (nuts = true) {
      notAllowed.append(Config.getNuts());
      notAllowed.append("|");
    }
    if (meat = true) {
      notAllowed.append(Config.getMeats());
      notAllowed.append("|");
    }
    notAllowed.deleteCharAt(notAllowed.length()-1);
    Pattern pattern = Pattern.compile(notAllowed.toString());
    try {
      PriorityQueue<Recipe> recpq = preCommand(command);
      List<JsonObject> guiResults = new ArrayList<>();
      for (int i = 0; i < 100; i ++) {
        JsonObject jsonRecipe = Config.getRecipeJson(recpq.poll().getId());
        jsonRecipe.get("tokens");
        guiResults.add(jsonRecipe);
      }
      return guiResults;
    } catch (IOException | ParseException | SQLException e) {
      throw new CommandException(e.getMessage());
    }
  }
}
