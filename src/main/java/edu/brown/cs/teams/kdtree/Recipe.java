package src.main.java.edu.brown.cs.teams.kdtree;

import java.util.List;

public class Recipe extends CartesianPoint {
  private int id;

  /**
   * Constructor for Cartesian point.
   *
   * @param embedding double array storing position of point
   * @param id a String
   */
  public Recipe(double[] embedding, int id) {
    super(embedding);
    this.id = id;
  }

  public int getId() {
    return id;
  }

  // Gets cosine similarity
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
}
