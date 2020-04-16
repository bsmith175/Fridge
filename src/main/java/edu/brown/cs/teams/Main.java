package src.main.java.edu.brown.cs.teams;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import src.main.java.edu.brown.cs.teams.kdtree.KDTree;
import src.main.java.edu.brown.cs.teams.kdtree.KDTreeNode;
import src.main.java.edu.brown.cs.teams.kdtree.Recipe;


public class Main {

  public static double[] arraysSum(double[][] arrays) {
    double[] result = new double[100];
    for (int i = 0; i < result.length; i ++){
      for (double[] currArr: arrays) {
        result[i] += currArr[i];
      }
    }
    return result;
  }

  public static void main(String[] args) throws FileNotFoundException {
    JSONParser obj = new JSONParser();
    try (FileReader reader = new FileReader("data/foodVecs.json")){
      JSONParser parser = new JSONParser();
      Gson gson = new Gson();
      JSONObject object = (JSONObject) parser.parse(reader);
      // Mackerel hash
      double[] mackerel = gson.fromJson(object.get("Mackerel").toString(), double[].class);
      double[] horseRad = gson.fromJson(object.get("Horseradish").toString(), double[].class);
      double[] vegetableOil = gson.fromJson(object.get("Vegetable oil").toString(), double[].class);
      double[] eggs = gson.fromJson(object.get("Eggs").toString(), double[].class);
      double[] potatoes = gson.fromJson(object.get("Potatoes").toString(), double[].class);
      double[] recipe1Sum = arraysSum(new double[][]{mackerel, horseRad, vegetableOil, eggs, potatoes});
      Recipe mackHash = new Recipe(recipe1Sum, 1);
      // Biscuits
      double[] flour = gson.fromJson(object.get("Self raising flour").toString(), double[].class);
      double[] cream = gson.fromJson(object.get("Cream").toString(), double[].class);
      double[] recipe2Sum = arraysSum(new double[][]{flour, cream});
      Recipe biscuits = new Recipe(recipe2Sum, 2);
      // Cookies
      double[] butter = gson.fromJson(object.get("Butter").toString(), double[].class);
      double[] sugar = gson.fromJson(object.get("Sugar").toString(), double[].class);
      double[] all_purpose = gson.fromJson(object.get("All purpose flour").toString(), double[].class);
      double[] confec_sugar = gson.fromJson(object.get("Confectioners sugar").toString(), double[].class);
      double[] recipe3Sum = arraysSum(new double[][]{butter, sugar, all_purpose, confec_sugar});
      Recipe cookies = new Recipe(recipe3Sum, 3);
      // Glazed baby carrots
      double[] baby_carr = gson.fromJson(object.get("Baby carrots").toString(), double[].class);
      double[] water = gson.fromJson(object.get("Water").toString(), double[].class);
      double[] brown_sugar = gson.fromJson(object.get("Brown sugar").toString(), double[].class);
      double[] recipe4Sum = arraysSum(new double[][]{baby_carr, water, butter, brown_sugar});
      Recipe baby_carrots = new Recipe(recipe4Sum, 4);
      // Chili and Jelly Meatballs
      double[] meatballs = gson.fromJson(object.get("Meatballs").toString(), double[].class);
      double[] chili_sauce = gson.fromJson(object.get("Chili sauce").toString(), double[].class);
      double[] jelly = gson.fromJson(object.get("Jelly").toString(), double[].class);
      double[] recipe5Sum = arraysSum(new double[][]{meatballs, chili_sauce, jelly});
      Recipe jellyMeatballs = new Recipe(recipe5Sum, 5);
      List<Recipe> recipes = new ArrayList<>();
      recipes.add(jellyMeatballs);
      recipes.add(baby_carrots);
      recipes.add(cookies);
      recipes.add(biscuits);
      recipes.add(mackHash);
      // Build kd tree
      KDTree<Recipe> tree = new KDTree<Recipe>(100);
      tree.buildKDTree(recipes);

      // Get vector of user's queries
      double[] querySum = arraysSum(new double[][]{mackerel, horseRad, vegetableOil, eggs});
      List<Recipe> neighbors = tree.getNeighbors(5, querySum);

      int minId = neighbors.get(0).getId();
      double minDistance = Integer.MAX_VALUE;
      for (Recipe recipe : neighbors) {
        if (recipe.getDistance(querySum) < minDistance) {
          minId = recipe.getId();
          minDistance = recipe.getDistance(querySum);
        }
      }
      System.out.println(minId);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }


  }

}
