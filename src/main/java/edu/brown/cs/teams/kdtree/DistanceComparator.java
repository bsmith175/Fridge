package edu.brown.cs.teams.kdtree;

import java.util.Comparator;

/**
 * Comparator to compare the distance of points from a single fixed target position.
 */
public class DistanceComparator implements Comparator<CartesianPoint> {
  private double[] target;

  /**
   * Constructor for Comparator.
   * @param targetPos target position that everything will be compared to.
   */
  public DistanceComparator(double[] targetPos) {
    this.target = targetPos;
  }

  /**
   * Calculates whether point p1 or p2 is closer to the target position.
   * @param p1 a point
   * @param p2 a point
   * @return 1 if p1 is closer than p2, -1 if p1 is further than p2 from the target,
   * and 0 if they are equidistant.
   */
  @Override
  public int compare(CartesianPoint p1, CartesianPoint p2) {
    double distanceFromP1 = p1.getDistance(target);
    double distanceFromP2 = p2.getDistance(target);

    if (distanceFromP1 < distanceFromP2) {
      return 1;
    } else if (distanceFromP1 > distanceFromP2) {
      return -1;
    } else {
      return 0;
    }
  }
}
