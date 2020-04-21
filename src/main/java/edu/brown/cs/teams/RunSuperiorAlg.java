package src.main.java.edu.brown.cs.teams;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import src.main.java.edu.brown.cs.teams.io.Command;
import src.main.java.edu.brown.cs.teams.io.CommandException;
import src.main.java.edu.brown.cs.teams.recipe.Ingredient;
import src.main.java.edu.brown.cs.teams.recipe.Recipe;
import src.main.java.edu.brown.cs.teams.state.Config;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RunSuperiorAlg implements Command {
  @Override
  public String runCommand(String[] command) throws CommandException {
    if (command.length < 2) {
      throw new CommandException("ERROR: Must enter an ingredient");
    }
    try {
      Gson gson = new Gson();
      FileReader reader = new FileReader("data/ingredient_vectors.json");
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(reader);


      List<Ingredient> ingredients = new ArrayList<>();
      for (String word : Arrays.copyOfRange(command, 1, command.length)) {
        double[] embedding = gson.fromJson(object.get(word).toString(),
                double[].class);
        Ingredient ingredient = new Ingredient(word, embedding);
        ingredients.add(ingredient);
      }

      for (Recipe recipe : Config.getFullRecipes()) {
        recipe.compareToIngredients(ingredients);
      }
      Recipe first = Config.getFullRecipes().peek();
      System.out.println(first.getId());
      return first.getId();
    } catch (IOException | ParseException e) {
      throw new CommandException(e.getMessage());
    }
  }
}
