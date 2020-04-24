package src.main.java.edu.brown.cs.teams.database;

import org.json.simple.parser.JSONParser;
import src.main.java.edu.brown.cs.teams.io.CommandException;
import src.main.java.edu.brown.cs.teams.recipe.Ingredient;
import src.main.java.edu.brown.cs.teams.recipe.MinimalRecipe;
import org.json.simple.JSONObject;

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
import java.util.*;
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

  /**
   * Method to get a "full recipe" (ingredients and not just vector) from the
   * recipe database.
   *
   * @param vectorFileName the json mapping words to vectors.
   * @return a list of all the recipes with their ingredients mapped.
   * @throws SQLException
   */
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
          rs.close();
        }
        prep.close();
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


  /**
   * Gets the content needed to display the recipe in labeled form, given its ID.
   * @param id  - the recipe ID.
   * @return - JsonObject of the recipe's content (everything except tokens)
   *         - null if the recipe was not in the database
   * @throws SQLException - if exception occurs while querying database
   */
  public JsonObject getRecipeContentFromID(String id) throws SQLException {
    String query = "SELECT id, name, author, description, ingredients, " +
            "method, time, servings, imageURL" +
            " FROM recipe WHERE recipe.id=" + "id" +
            ";";
    PreparedStatement prep = conn.prepareStatement(query);
    ResultSet rs = prep.executeQuery();
    if (rs.next()) {
      JsonObject recipe = new JsonObject();
      recipe.addProperty("id", rs.getInt(1));
      recipe.addProperty("name", rs.getString(2));
      recipe.addProperty("author", rs.getString(3));
      recipe.addProperty("description", rs.getString(4));
      recipe.addProperty("ingredients", rs.getString(5));
      recipe.addProperty("method", rs.getString(6));
      recipe.addProperty("time", rs.getString(7));
      recipe.addProperty("servings", rs.getString(8));
      recipe.addProperty("imageURL", rs.getString(9));
      return recipe;
    } else {
      return null;
    }
  }


}
