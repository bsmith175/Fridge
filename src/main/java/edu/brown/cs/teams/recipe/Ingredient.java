package edu.brown.cs.teams.recipe;

import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.kdtree.CartesianPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ingredient extends CartesianPoint {
  private String id;
  private String category;
  private double distance;


  public Ingredient(String id, double[] vector) {
    super(vector);
    this.id = id;
  }

  public String getCategory() {
    return this.category;
  }

  public double[] getVec() {
    return super.getPosition();
  }

  public String getId() {
    return this.id;
  }

  /**
   * Gets the distance of this ingredient to a nearest ingredient. Useful for
   * the recipe class to not have to repeat calculations.
   * @return the distance double
   */
  public double getDistance() {
    return this.distance;
  }

  /**
   * Sets the distance of this ingredient to a nearest ingredient. Useful for
   * the recipe class to not have to repeat calculations.
   * @param distance
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }


  /**
   * Gives the most likely candidate to represent a recipe ingredient in
   * the user ingredient list.
   *
   * @param ingredients the user ingredient list
   * @return the closest vector ingredient to the recipe ingredient
   */
  public Ingredient generateCandidate(
      List<Ingredient> ingredients) {
    int num = ingredients.size();
    Ingredient best = null;
    double closest = 0.0;
    for (Ingredient i : ingredients) {
      double similarity = super.getDistance(i.getVec());
      if (similarity > closest) {
        best = i;
        closest = similarity;
      }
    }
    if (closest > 1 - num * AlgUtils.SIMILARITY_FACTOR) {
      return best;
    } else {
      return null;
    }
  }


}
