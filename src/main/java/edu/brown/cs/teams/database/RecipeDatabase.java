package edu.brown.cs.teams.database;

import com.google.gson.Gson;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.MinimalRecipe;
import edu.brown.cs.teams.recipe.Recipe;
import edu.brown.cs.teams.state.Config;
import org.json.JSONException;
//import org.json.JSONObject;
import org.json.simple.JSONObject;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import edu.brown.cs.teams.io.CommandException;

public class RecipeDatabase {

  private Connection conn;
  private PreparedStatement prep = null;
  private ResultSet rs = null;
  private String dbFileName;
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

  public void makeTable() throws SQLException {
    System.out.println("makeTable");
    prep = conn.prepareStatement("CREATE TABLE recipe("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT,"
            + "author TEXT,"
            + "description TEXT,"
            + "ingredients TEXT,"
            + "tokens TEXT,"
            + "method TEXT,"
            + "time TEXT,"
            + "servings TEXT,"
            + "imageURL TEXT);");
    prep.executeUpdate();
  }

  public void parseJson() throws JSONException {
    try (FileReader reader = new FileReader("data/recipe.json")) {
      Tokenizer converter = new Tokenizer();
      JSONParser parser = new JSONParser();
      Gson gson = new Gson();
      JSONArray array = (JSONArray) parser.parse(reader);
      int id = 0;
      for (int i = 0; i < array.size(); i++) {
        System.out.println("_______" + i);
        JSONObject e = (JSONObject) array.get(i);

        JSONArray recipeArray = (JSONArray) e.get("ingredients");
        JSONArray timeArray = (JSONArray) e.get("time");
        JSONArray methodArray = (JSONArray) e.get("method");


        List<String> res = new ArrayList<>();
        res = new Gson().fromJson(String.valueOf(recipeArray), ArrayList.class);
        List<String> tokens = converter.parseIngredients(res);
        String jsonToks = new Gson().toJson(tokens);
        if ((String) e.get("author") != null) {
          prep = conn.prepareStatement(
                  "INSERT INTO recipe VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
          prep.setString(1, String.valueOf(id));
          prep.setString(2, (String) e.get("name"));
          prep.setString(3, (String) e.get("author"));
          prep.setString(4, (String) e.get("description"));
          prep.setString(5, recipeArray.toJSONString());
          prep.setString(6, (String) jsonToks);
          prep.setString(7, (String) methodArray.toJSONString());
          prep.setString(8, (String) timeArray.toJSONString());
          prep.setString(9, (String) e.get("servings"));
          prep.setString(10, (String) e.get("img_url"));
          prep.addBatch();
          prep.executeBatch();
          id ++;
        }
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }


}