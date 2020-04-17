package src.main.java.edu.brown.cs.teams.Main;

import src.main.java.edu.brown.cs.teams.database.RecipeDatabase;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import org.json.JSONException;
public class Main {
  public static void main(String[] args) {
    try {
      RecipeDatabase rd = new RecipeDatabase("data/recipe.sqlite3");
      rd.parseJson();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (JSONException e){
      e.printStackTrace();

    }

  }
}
