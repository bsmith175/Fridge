package edu.brown.cs.teams.recipe;

import com.google.gson.JsonObject;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.kdtree.CartesianPoint;
import edu.brown.cs.teams.algorithms.AlgUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Recipe extends CartesianPoint {
  private int id;
  private Set<Ingredient> ingredients;
  private double similarity;

  /**
   * Constructor for Cartesian point.
   *
   * @param embedding double array storing position of point
   * @param id        a String
   * @param ingredients a Set
   */
  public Recipe(double[] embedding, int id, Set<Ingredient> ingredients) {
    super(embedding);
    this.id = id;
    this.ingredients = ingredients;
    this.similarity = 0.0;
  }

  /**
   * getter method for ingredients.
   * @return the set of ingredients in the recipe
   */
  public Set<Ingredient> getIngredients() {
    return this.ingredients;
  }

  /**
   * getter method for id.
   * @return the set of ingredients in the recipe
   */
  public int getId() {
    return id;
  }

  /**
   * Generates the closest list of ingredients to a recipe from an ingredient
   * list.
   *
   * @param ingreds a user list of ingredients
   * @throws CommandException
   * @return an approximation of the recipe within the user ingredients
   */
  public double compareToIngredients(
          List<Ingredient> ingreds) throws CommandException {
    try {
      List<Ingredient> candidateList = new ArrayList<>();
      //generate user candidate for every recipe ingredient
      for (Ingredient ing : this.ingredients) {
        Ingredient candidate = ing.generateCandidate(ingreds);
        if (candidate != null) {
          candidateList.add(candidate);
        }
      }
      //approxmating a reicpe vector from user candidate ingredients
      double[] candidatesVec = AlgUtils.ingredAdd(candidateList);
      //distance from recipe to user approximated recipe
      double distance = 0.0;
      if (candidateList.size() != 0) {
        distance = super.getDistance(candidatesVec);
      }
      //penalizing based on number of missing ingredients
     double similarity =
              distance * ((this.ingredients.size() - candidateList.size()) * -0.001
                  + 1);
      return similarity;
    } catch (Exception e) {
      throw new CommandException(e.getMessage());
    }
  }

  /**
   * Method to get the json version of a recipe (to output to gui).
   * @return a json that can be parsed in the front-end
   * @throws SQLException for invalid query
   */
  public JsonObject getRecipeJson() throws SQLException {
    return AlgUtils.getRecipeDb().getRecipeContentFromID(this.id);
  }


  /**
   * gets the similarity score of this recipe.
   *
   * @return the double score
   */
  public double getSimilarity() {
    return this.similarity;
  }
}
