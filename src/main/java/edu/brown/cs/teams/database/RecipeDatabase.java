package edu.brown.cs.teams.database;

import com.google.gson.Gson;
import org.json.JSONException;
//import org.json.JSONObject;
import org.json.simple.JSONObject;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeDatabase {

  private Connection conn;
  private PreparedStatement prep = null;
  private ResultSet rs = null;

  public RecipeDatabase(String filename) throws ClassNotFoundException, SQLException,
          FileNotFoundException {
    if (!Files.exists(Path.of(filename))) {
      throw new FileNotFoundException("Database file not found.");
    }
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
    Statement stat = conn.createStatement();
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