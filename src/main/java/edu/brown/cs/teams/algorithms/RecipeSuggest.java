package edu.brown.cs.teams.algorithms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.io.Command;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.MinimalRecipe;
import edu.brown.cs.teams.recipe.Recipe;
import edu.brown.cs.teams.recipe.RecipeDistanceComparator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Class to suggest recipe, implements the command interface.
 */
public class RecipeSuggest implements Command {

  private boolean dairy;
  private boolean meats;
  private boolean nuts;
  public static final int NUM_RESULTS_PRE = 400;
  public static final int NUM_RESULTS_FINAL = 100;

  public RecipeSuggest(boolean meats, boolean dairy, boolean nuts) {
    this.meats = meats;
    this.dairy = dairy;
    this.nuts = nuts;
  }

  /**
   * executable repl command.
   *
   * @param command user input string split on whitespace
   * @return the string output of the algorithm
   * @throws CommandException when command is invalid
   */
  @Override
  public String runCommand(String[] command) throws CommandException {
    if (command.length < 2) {
      throw new CommandException("ERROR: Must enter an ingredient");
    }
    PriorityQueue<Recipe> recpq = preCommand(command);
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < 5; i++) {
      Recipe first = recpq.poll();
      output.append("1.\r\n");
      output.append("ID: ").append(first.getId()).append("\r\n");
      output.append("Tokens: ");
      for (Ingredient ingr : first.getIngredients()) {
        output.append(ingr.getId()).append(", ");
      }
      output.delete(output.length() - 2, output.length()).append("\r\n");
      output.append("----------");
    }
    return output.toString();
  }

  /**
   * helper method to reduce rendundant code between repl and gui output.
   *
   * @param command the user input parsed on whitespace
   * @return the sorted recipes
   */
  private PriorityQueue<Recipe> preCommand(String[] command)
          throws CommandException {
    List<Ingredient> ingredients = new ArrayList<>();
    for (String word : Arrays.copyOfRange(command, 0, command.length)) {
      word = word.replaceAll("\"", "");
      try {
        Ingredient ingredient = new Ingredient(word);
        ingredients.add(ingredient);
      } catch (NullPointerException e) {
        System.out.println(word + " is not a valid ingredient in our "
                + "database. It will be ignored");
      }
    }

    for (Recipe recipe : AlgUtils.getFullRecipes()) {
      recipe.compareToIngredients(ingredients);
    }
    List<Recipe> reclist = AlgUtils.getFullRecipes();
    PriorityQueue<Recipe> recpq =
            new PriorityQueue<>(new RecipeDistanceComparator());

    recpq.addAll(reclist);
    return recpq;
  }

  @Override
  public List<JsonObject> runForGui(String[] command)
          throws CommandException, SQLException {
    if (command.length < 1) {
      throw new CommandException("ERROR: Must enter an ingredient");
    }
    StringBuilder notAllowed = new StringBuilder();
    boolean any = false;
    if (this.dairy) {
      notAllowed.append(AlgUtils.getDairy());
      notAllowed.append("|");
      any = true;
    }
    if (this.nuts) {
      notAllowed.append(AlgUtils.getNuts());
      notAllowed.append("|");
      any = true;
    }
    if (this.meats) {
      notAllowed.append(AlgUtils.getMeats());
      notAllowed.append("|");
      any = true;
    }
    if (any) {
      notAllowed.deleteCharAt(notAllowed.length() - 1);
    }
    String restrictions = notAllowed.toString();
    PriorityQueue<Recipe> recpq = preCommand(command);
    List<JsonObject> guiResults = new ArrayList<>();
    for (int i = 0; i < NUM_RESULTS_PRE; i++) {
      Recipe rec = recpq.poll();
      JsonObject jsonRecipe = rec.getRecipeJson();
      Gson gson = new Gson();
      double match =  Math.floor(rec.getSimilarity() * 100) ;
      jsonRecipe.addProperty("percentMatch", match);
      String tokenList =
              gson.fromJson(jsonRecipe.get("ingredients"), String.class);
      if (tokenList.replaceFirst(restrictions, "").length()
              == tokenList.length()) {
        guiResults.add(jsonRecipe);
      }
    }
    guiResults = guiResults.subList(0, Math.min(NUM_RESULTS_FINAL,
            guiResults.size()));
    return guiResults;

  }


}
