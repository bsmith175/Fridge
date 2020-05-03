package edu.brown.cs.teams.recipe;

import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.kdtree.CartesianPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ingredient extends CartesianPoint{
  private String id;
  private double distance;


  public Ingredient(String id) {
    super(new double[]{});
    this.id = id;
  }

  public double[] getVec() {
    return AlgUtils.getVectorMap().get(id);
  }

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
      double similarity = this.getDistance(i.getVec());
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

  @Override
  public double getDistance(double[] target) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    double[] vec = getVec();
    for (int i = 0; i < target.length; i++) {
      dotProduct += vec[i] * target[i];
      normA += Math.pow(vec[i], 2);
      normB += Math.pow(target[i], 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }


}
