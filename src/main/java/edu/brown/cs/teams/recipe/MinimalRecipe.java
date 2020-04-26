package edu.brown.cs.teams.recipe;

import edu.brown.cs.teams.kdtree.CartesianPoint;
import edu.brown.cs.teams.kdtree.DistanceMetric;

import java.util.HashSet;

public class MinimalRecipe extends CartesianPoint implements DistanceMetric {
  private String id;
  /**
   * Constructor for Cartesian point.
   *
   * @param embedding   double array storing position of point
   * @param id          a String
   */
  public MinimalRecipe(double[] embedding, String id) {
    super(embedding);
    this.id = id;
  }

  @Override
  public double getDistance(double[] target) {
    double sum = 0.0;
    for(int i=0;i<target.length;i++) {
      sum = sum + Math.pow((target[i]-super.getPosition()[i]),2.0);
    }
    return sum;
  }

  /**
   * Getter method for the id
   * @return
   */
  public String getId() {
    return id;
  }
}
