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
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < target.length; i++) {
      dotProduct += super.getPosition()[i] * target[i];
      normA += Math.pow(super.getPosition()[i], 2);
      normB += Math.pow(target[i], 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }

  /**
   * Getter method for the id
   * @return
   */
  public String getId() {
    return id;
  }
}
