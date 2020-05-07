package edu.brown.cs.teams.algorithms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.io.Command;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.Recipe;
import edu.brown.cs.teams.recipe.RecipeDistanceComparator;
import java.sql.SQLException;
import java.util.*;

/**
 * Class to suggest recipe, implements the command interface.
 */
public class RecipeSuggest implements Command {

  private boolean dairy;
  private boolean meats;
  private boolean nuts;
  public static final int NUM_RESULTS_PRE = 400;
  public static final int NUM_RESULTS_FINAL = 100;
  private Map<Integer, Double> simMap = new HashMap<>();

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
  public String runCommand(String[] command)
          throws CommandException, SQLException {
    if (command.length < 2) {
      throw new CommandException("ERROR: Must enter an ingredient");
    }
    PriorityQueue<Integer> recpq = preCommand(command);
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < 5; i++) {
      int first = recpq.poll();
      output.append("1.\r\n");
      output.append("ID: ").append(first).append("\r\n");
      output.append("Tokens: ");
      for (Ingredient ingr
              : AlgUtils.getRecipeDb().getRecipe(first).getIngredients()) {
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
  private PriorityQueue<Integer> preCommand(String[] command)
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
      double sim = recipe.compareToIngredients(ingredients);
      this.simMap.put(recipe.getId(), sim);
    }
    PriorityQueue<Integer> recpq =
            new PriorityQueue<>(new RecipeDistanceComparator(this.simMap));
    recpq.addAll(this.simMap.keySet());
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
    PriorityQueue<Integer> recpq = preCommand(command);
    List<JsonObject> guiResults = new ArrayList<>();
    for (int i = 0; i < NUM_RESULTS_PRE; i++) {
      int rec = recpq.poll();
      JsonObject jsonRecipe =
              AlgUtils.getRecipeDb().getRecipeContentFromID(rec);
      Gson gson = new Gson();
      double match =  Math.floor(this.simMap.get(rec) * 100);
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
