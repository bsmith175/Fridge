package src.main.java.edu.brown.cs.teams.recipe;

import src.main.java.edu.brown.cs.teams.kdtree.CartesianPoint;
import src.main.java.edu.brown.cs.teams.kdtree.DistanceMetric;

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
    double diff_square_sum = 0.0;
    for (int i = 0; i < this.getPosition().length; i++) {
      diff_square_sum += (this.getPosition()[i] - target[i]) * (this.getPosition()[i] - target[i]);
    }
    return Math.sqrt(diff_square_sum);
  }

  /**
   * Getter method for the id
   * @return
   */
  public String getId() {
    return id;
  }
}
