package edu.brown.cs.teams.kdtree;

/**
 * Interface for distance metric.
 */
public interface DistanceMetric {
  /**
   * Method to get the distance to a target position.
   * @param target a double array
   * @return the distance to the target
   */
  double getDistance(double[] target);
}
