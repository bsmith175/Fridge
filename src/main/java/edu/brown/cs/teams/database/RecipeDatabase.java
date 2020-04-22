package src.main.java.edu.brown.cs.teams.database;

import org.json.simple.parser.JSONParser;
import src.main.java.edu.brown.cs.teams.io.CommandException;
import src.main.java.edu.brown.cs.teams.recipe.Ingredient;
import src.main.java.edu.brown.cs.teams.recipe.MinimalRecipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import com.google.gson.*;
import src.main.java.edu.brown.cs.teams.recipe.Recipe;
import src.main.java.edu.brown.cs.teams.state.Config;


public class RecipeDatabase {
  private static Connection conn = null;
  private String dbFileName;
  private String vectorFileName;

  /**
   * Constructor for RecipeDatabase. Establishes a connection to the db and
   * verifies format.
   *
   * @param dbFileName the path to the database
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws CommandException
   */
  public RecipeDatabase(String dbFileName)
          throws SQLException, ClassNotFoundException, CommandException {
    File db = new File(dbFileName);
    if (!db.exists()) {
      throw new CommandException("ERROR: database file does not exist: "
              + dbFileName);
    }

    this.dbFileName = dbFileName;
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + dbFileName;
    conn = DriverManager.getConnection(urlToDB);

    // these two lines tell the database to enforce
    // foreign keys during operations
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
    try {
      verifyTables();
    } catch (SQLException e) {
      throw new CommandException("ERROR: SQL database is malformed: "
              + this.dbFileName);
    }
  }

  /**
   * Dummy method to verify all the columns in the recipe table are there.
   *
   * @throws SQLException
   * @throws CommandException
   */
  public void verifyTables() throws SQLException, CommandException {
    String query =
            "SELECT recipe.id, recipe.name, recipe.author, recipe.description,"
                    +
                    " recipe.ingredients, recipe.tokens, recipe.time, recipe.servings"
                    + " FROM recipe "
                    + "LIMIT 1;";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      try (ResultSet rs = prep.executeQuery()) {
        if (!rs.next()) {
          throw new CommandException(
                  "ERROR: database has no recipes: " + dbFileName);
        }
      }
    }
  }

  public List<MinimalRecipe> getRecipes(String vectorFileName)
          throws SQLException {
    String query = "SELECT * FROM recipe";
    JSONParser parser = new JSONParser();
    Gson gson = new Gson();
    List<MinimalRecipe> recipes = new ArrayList<>();
    try (FileReader reader = new FileReader(vectorFileName)) {
      JSONObject object = (JSONObject) parser.parse(reader);
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        try (ResultSet rs = prep.executeQuery()) {
          while (rs.next()) {
            String[] tokens = rs.getString(6)
                    .substring(1, rs.getString(6).length() - 1)
                    .replaceAll("\"", "")
                    .split(",");
            Set<String> newTokens = new HashSet<>();
            double[][] embeddings = new double[tokens.length][300];
            for (int i = 0; i < tokens.length; i++) {
              if (object.get(tokens[i]) == null) {
                embeddings[i] = new double[300];
              } else {
                embeddings[i] = gson.fromJson(object.get(tokens[i]).toString(),
                        double[].class);
              }
              newTokens.add(tokens[i]);
            }
            double[] totalEmbedding = Config.arrayAdd(embeddings);
            String id = rs.getString(1);
            MinimalRecipe recipe = new MinimalRecipe(totalEmbedding, id);
            recipes.add(recipe);
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return recipes;
  }


  public String getRecipe(String id) throws CommandException {
    String query = "SELECT * FROM recipe WHERE recipe.id = ?";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, id);
      try (ResultSet rs = prep.executeQuery()) {
        String result = "";
        while (rs.next()) {
          result += rs.getString(1) + " " + rs.getString(2) +
                  " " + rs.getString(6) + "\n";
        }
        return result;
      }
    } catch (SQLException e) {
      throw new CommandException("Recipe " + id + " does not exist.");
    }
  }


  public List<Recipe> getFullRecipes(String vectorFileName)
          throws SQLException {
    String query = "SELECT id, tokens FROM recipe";
    JSONParser parser = new JSONParser();
    Gson gson = new Gson();
    List<Recipe> recipes = new ArrayList<>();
    try (FileReader reader = new FileReader(vectorFileName)) {
      JSONObject object = (JSONObject) parser.parse(reader);
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        try (ResultSet rs = prep.executeQuery()) {
          while (rs.next()) {
            String[] tokens = rs.getString(2)
                    .substring(1, rs.getString(2).length() - 1)
                    .replaceAll("\"", "")
                    .split(",");
            Set<Ingredient> newTokens = new HashSet<>();
            double[][] embeddings = new double[tokens.length][300];

            for (int i = 0; i < tokens.length; i++) {
              embeddings[i] = gson.fromJson(object.get(tokens[i]).toString(),
                      double[].class);
              if (tokens[i] != "") {
                newTokens.add(new Ingredient(tokens[i], embeddings[i]));
              }
            }
            double[] totalEmbedding = Config.arrayAdd(embeddings);
            String id = rs.getString(1);
            Recipe recipe = new Recipe(totalEmbedding, id, newTokens);
            recipes.add(recipe);
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return recipes;
  }
}
