package src.main.java.edu.brown.cs.teams.io;


import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import src.main.java.edu.brown.cs.teams.RunKDAlg;
import src.main.java.edu.brown.cs.teams.RunSuperiorAlg;
import src.main.java.edu.brown.cs.teams.database.RecipeDatabase;
import src.main.java.edu.brown.cs.teams.algorithms.AlgMain;
import src.main.java.edu.brown.cs.teams.recipe.MinimalRecipe;
import src.main.java.edu.brown.cs.teams.state.Config;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Main {
  private static REPL repl;


  public static void main(String[] args) throws SQLException, CommandException,
      ClassNotFoundException,
      IOException, ParseException {
    new AlgMain();
    System.out.println("Getting recipes");
    RecipeDatabase db = new RecipeDatabase("data/recipe.sqlite3");
    AlgMain.setDb(db);
    Config.setDb(db);
    Config.buildRecList();
//    List<MinimalRecipe> recipes = AlgMain.getDb().getRecipes("data/ingredient_vectors.json");
//    AlgMain.setRecipeList(recipes);
//    System.out.println("Building KD Tree");
//    AlgMain.setTree(AlgMain.getTree().buildKDTree(recipes));
    System.out.println("Success! KD Tree is ready for querying.");
    HashMap<String, Command> commands = new HashMap<>();
    commands.put("KDAlg", new RunKDAlg());
///////////////////////////////
    //DO NOT DELETE THIS PLEASE OR IM GONNA FORGET ITS HERE

    System.out.println("the cavalry has arrived");
    commands.put("recommend", new RunSuperiorAlg());
    System.out.println("gg nate (jk this is gonna be extremely buggy right " +
            "now)");

    Main.repl = new REPL(commands);
    Main.repl.runREPL();
  }

}
