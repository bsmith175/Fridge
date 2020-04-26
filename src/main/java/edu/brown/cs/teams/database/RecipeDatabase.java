package edu.brown.cs.teams.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.login.AccountUser;
import edu.brown.cs.teams.recipe.Ingredient;
import edu.brown.cs.teams.recipe.MinimalRecipe;
import edu.brown.cs.teams.recipe.Recipe;
import edu.brown.cs.teams.algorithms.Config;
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

  /**
   * Constructs a postgresql RecipeDatabase. Connects to database server.
   */
  public RecipeDatabase(String url, String user, String pwd, Boolean init) throws ClassNotFoundException,
          SQLException, CommandException {
    Class.forName("org.postgresql.Driver");
    conn = DriverManager.getConnection(url, user, pwd);


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


  //-------------------------- User tables setup --------------------------------


  /**
   * creates the user table. This table has three columns:
   *      uid (TEXT): the unique string identifying the user (primary key)
   *      name (TEXT): The user's first name. Is not unique
   *      profile (TEXT): The path to the user's profile image.
   *
   * @throws SQLException - if exception occurs while making table.
   */
  public void makeUserTable() throws SQLException {
    PreparedStatement prep = conn.prepareStatement("CREATE TABLE user("
            + "uid TEXT PRIMARY KEY,"
            + "name TEXT,"
            + "profile TEXT);");
    prep.executeUpdate();
  }

  /**
   * The favorite table is a junction table between recipes and users. It links every recipe
   * that is a favorite to each of the users that have it as a favorite.
   *
   * @throws SQLException
   */
  public void makeFavTable() throws SQLException {
    PreparedStatement prep = conn.prepareStatement("CREATE TABLE favorite("
            + "recipeId TEXT FOREIGN KEY REFERENCES recipe(id),"
            + "uid TEXT FOREIGN KEY REFERENCES user(uid);");
    prep.executeUpdate();
  }

  //------------------------- Recipe table queries----------------

  public List<MinimalRecipe> getRecipes(String vectorFileName)
      throws CommandException {
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
      } catch (SQLException e) {
        throw new CommandException(e.getMessage());
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



  //-------------------------- User tables methods --------------------------------

  /**
   * Adds a new user to the database.
   * @param user - the User to be added to the database
   * @throws SQLException - if user could not be added
   *                      - if user already exists
   */
  public void addNewUser(AccountUser user) throws SQLException {
    String uid = user.getUid();
    String name = user.getName();
    String profilePic = user.getProfile();

    PreparedStatement prep = conn.prepareStatement("INSERT INTO user VALUES (?, ?, ?);");
    prep.setString(1, uid);
    prep.setString(2, name);
    prep.setString(3, profilePic);
    prep.addBatch();
    prep.executeUpdate();
  }


  /**
   * Queries a user's favorite recipe list, given a user ID.
   * @param uid - User ID
   * @return - A List of recipe IDs
   * @throws SQLException - if exception occurs during query
   */
  public List<Integer> getFavorites(String uid) throws SQLException{
    PreparedStatement prep = conn.prepareStatement("SELECT recipeId FROM favorite WHERE uid= ?");
    prep.setString(1, uid);
    ResultSet res = prep.executeQuery();
    List<Integer> ret = new ArrayList<Integer>();
    while (res.next()) {
      ret.add(res.getInt(1));
    }
    return ret;
  }

  /**
   * Gets the content needed to display the recipe in labeled form, given its ID.
   * @param id  - the recipe ID.
   * @return - JsonObject of the recipe's content (everything except tokens)
   *         - null if the recipe was not in the database
   * @throws SQLException - if exception occurs while querying database
   */
  public JsonObject getRecipeContentFromID(String id) throws SQLException {
    String query = "SELECT * FROM recipe WHERE recipe.id= ?";
    PreparedStatement prep = conn.prepareStatement(query);
    prep.setString(1, id);
    ResultSet rs = prep.executeQuery();
    if (rs.next()) {
      JsonObject recipe = new JsonObject();
      recipe.addProperty("id", rs.getInt(1));
      recipe.addProperty("name", rs.getString(2));
      recipe.addProperty("author", rs.getString(3));
      recipe.addProperty("description", rs.getString(4));
      recipe.addProperty("ingredients", rs.getString(5));
      recipe.addProperty("method", rs.getString(7));
      recipe.addProperty("time", rs.getString(8));
      recipe.addProperty("servings", rs.getString(9));
      recipe.addProperty("imageURL", rs.getString(10));
      return recipe;
    } else {
      return null;
    }
  }

  /**
   * Attemps to add a recipe to the user's favorites list.
   * @param rid - ID of recipe
   * @param uid - ID of user
   * @return  True
   *             -if recipe was successfully added to favorites list
   *          False
   *              - if recipe was already in user's favorites list
   * @throws SQLException - if exception occurs while updating database
   */
  public Boolean addToFavorites(String rid, String uid) throws SQLException {
    String check = "SELECT EXISTS(SELECT * FROM favorite WHERE recipeId= ?  AND uid= ?);";
    PreparedStatement prep = conn.prepareStatement(check);
    prep.setString(1, rid);
    prep.setString(2, uid);
    ResultSet rs = prep.executeQuery();
    rs.next();

    if (rs.getBoolean(1)) {
      return false;
    }
    prep  = conn.prepareStatement("INSERT INTO favorite VALUES(?, ?)");
    prep.setString(1, rid);
    prep.setString(2, uid);
    prep.addBatch();
    prep.executeUpdate();
    return true;
  }
}