package edu.brown.cs.teams.recipe;

import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.kdtree.CartesianPoint;

import java.util.List;

/**
 * class for an ingredient.
 */
public class Ingredient extends CartesianPoint {
  private String id;
  private double distance;

  /**
   * constructor for an ingredient.
   *
   * @param id the ingredient id
   */
  public Ingredient(String id) {
    super(new double[]{});
    this.id = id;
  }

  /**
   * gets the vector from the word embedding map.
   *
   * @return the word embedding (double[])
   */
  @Override
  public double[] getPosition() {
    return AlgUtils.getVectorMap().get(id);
  }

  /**
   * gets the id of the ingredient.
   *
   * @return id of this ingredient
   */
  public String getId() {
    return this.id;
  }

  /**
   * Gets the distance of this ingredient to a nearest ingredient. Useful for
   * the recipe class to not have to repeat calculations.
   *
   * @return the distance double
   */
  public double getDistance() {
    return this.distance;
  }


  /**
   * Sets the distance of this ingredient to a nearest ingredient. Useful for
   * the recipe class to not have to repeat calculations.
   *
   * @param distance set the distance of this ingredient
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
      double similarity = this.getDistance(i.getPosition());
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
