package edu.brown.cs.teams.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.MinimalRecipe;
import edu.brown.cs.teams.recipe.Recipe;
import edu.brown.cs.teams.algorithms.AlgUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
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
   * Constructor for a sqlite RecipeDatabase. Establishes a connection to the db and
   * verifies format.
   *
   * @param dbFileName the path to the database
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws CommandException
   */

  public RecipeDatabase(String dbFileName, Boolean init)
          throws SQLException, ClassNotFoundException, CommandException {
    File db = new File(dbFileName);
    if (!db.exists()) {
      throw new CommandException("No such file " + dbFileName);
    }
//
//    try {
//      if (!db.createNewFile()) {
//        //deletes file contents if file already exists
//        new PrintWriter(db).close();
//      }
//    } catch (IOException e) {
//      throw new CommandException(("ERROR: IOException when creating db file"));
//    }

      this.dbFileName = dbFileName;
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + dbFileName;
      conn = DriverManager.getConnection(urlToDB);

    // these two lines tell the database to enforce
    // foreign keys during operations
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
    if (!init) {
      try {
        verifyTables();
      } catch (SQLException e) {
        throw new CommandException("ERROR: SQL database is malformed: "
                + this.dbFileName);
      }
    }
  }



  //-------------------------- recipe tables setup --------------------------------


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

  public void makeTable() throws SQLException {
    System.out.println("makeTable");
    prep = conn.prepareStatement("CREATE TABLE recipe("
            + "id SERIAL PRIMARY KEY ,"
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

      //create filewriter for ingredient terms file
      File termData = new File(Constants.INGREDIENT_TERMS_PATH);
      if (!termData.createNewFile()) {
        //deletes file contents if file already exists
        new PrintWriter(termData).close();
      }
      FileWriter termWriter = new FileWriter(termData);

      //create filewriter for trie data file
      File trieData = new File(Constants.TRIE_DATA_PATH);
      if (!trieData.createNewFile()) {
        //deletes file contents if file already exists
        new PrintWriter(trieData).close();
      }
      FileWriter trieWriter = new FileWriter(trieData);

      for (int i = 0; i < array.size(); i++) {
        System.out.println("_______" + i);
        JSONObject e = (JSONObject) array.get(i);

        JSONArray recipeArray = (JSONArray) e.get("ingredients");
        JSONArray timeArray = (JSONArray) e.get("time");
        JSONArray methodArray = (JSONArray) e.get("method");


        List<String> res = new ArrayList<>();
        res = new Gson().fromJson(String.valueOf(recipeArray), ArrayList.class);
        List<String> tokens = converter.parseIngredients(res);

        //write to trie and term files
        writeData(tokens, trieWriter, termWriter);



        String jsonToks = new Gson().toJson(tokens);
        if ((String) e.get("author") != null) {
          prep = conn.prepareStatement(
                  "INSERT INTO recipe VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
          prep.setInt(1, id);
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
      termWriter.close();
      trieWriter.close();
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

  private void writeData(List<String> ingredients, FileWriter trieFile, FileWriter termFile) throws IOException {

    for (String ingredient : ingredients) {
      trieFile.write(ingredient.replaceAll("\\s+", "\n") + "\n");
      termFile.write(ingredient.strip() + "\n");

    }
  }


  //------------------------- Recipe table queries----------------


  public List<Recipe> getFullRecipes(String vectorFileName)
      throws CommandException {
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

            for (int i = 0; i < tokens.length; i++) {
              double[] embedding = gson.fromJson(object.get(tokens[i]).toString(),
                  double[].class);
              AlgUtils.getVectorMap().put(tokens[i], embedding);
              if (tokens[i] != "") {
                newTokens.add(new Ingredient(tokens[i]));
              }
            }
            double[] totalEmbedding = AlgUtils.ingredAdd(newTokens);
            int id = rs.getInt(1);
            Recipe recipe = new Recipe(totalEmbedding, id, newTokens);
            recipes.add(recipe);
          }
        }
      }
    } catch (FileNotFoundException e) {
      throw new CommandException(e.getMessage());
    } catch (IOException e) {
      throw new CommandException(e.getMessage());
    } catch (ParseException e) {
      throw new CommandException(e.getMessage());
    } catch (SQLException e) {
      throw new CommandException(e.getMessage());
    }
    return recipes;
  }

  /**
   * Method to get an array of token ingredients for a recipe.
   */
  public String[] getTokenIngredients(int id) throws CommandException {
    try {
      String query = "SELECT tokens FROM recipe WHERE recipe.id= ?";
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();
      if (rs.next()) {
        String[] tokens = rs.getString(1)
            .substring(1, rs.getString(1).length() - 1)
            .replaceAll("\"", "")
            .split(",");
        return tokens;
      } else {
        throw new CommandException("No such recipe with id " + id);
      }
    } catch (SQLException e) {
      throw new CommandException(e.getMessage());
    }

  }

  /**
   * Gets the content needed to display the recipe in labeled form, given its ID.
   * @param id  - the recipe ID.
   * @return - JsonObject of the recipe's content (everything except tokens)
   *         - null if the recipe was not in the database
   * @throws SQLException - if exception occurs while querying database
   */
  public JsonObject getRecipeContentFromID(int id) throws SQLException {
    String query = "SELECT * FROM recipe WHERE recipe.id= ?";
    PreparedStatement prep = conn.prepareStatement(query);
    prep.setInt(1, id);
    ResultSet rs = prep.executeQuery();
    if (rs.next()) {
      JsonObject recipe = new JsonObject();
      recipe.addProperty("id", rs.getInt(1));
      recipe.addProperty("name", rs.getString(2));
      recipe.addProperty("author", rs.getString(3));
      recipe.addProperty("description", rs.getString(4));
      recipe.addProperty("ingredients", rs.getString(5));
      recipe.addProperty("tokens", rs.getString(6));
      recipe.addProperty("method", rs.getString(7));
      recipe.addProperty("time", rs.getString(8));
      recipe.addProperty("servings", rs.getString(9));
      recipe.addProperty("imageURL", rs.getString(10));
      return recipe;
    } else {
      return null;
    }
  }




}