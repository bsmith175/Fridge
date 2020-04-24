package edu.brown.cs.teams.kdtree;

import java.util.Comparator;

/**
 * Comparator to compare points by a specific axis.
 */
public class KDNodeComparator implements Comparator<CartesianPoint> {
  private int axis;

  /**
   * Constructor for the comparator that stores an axis to compare along.
   * @param axis integer representing the axis of relevance.
   */
  public KDNodeComparator(int axis) {
    this.axis = axis;
  }

  /**
   * Determines which point has a greater value in the axis of interest.
   * @param a a point.
   * @param b a point.
   * @return 1 if a has a greater value than b along the axis, -1 if b has a
   * greater value than a, 0 if equal.
   */
  @Override
  public int compare(CartesianPoint a, CartesianPoint b) {
    double aVal = a.getPositionAlongAxis(axis);
    double bVal = b.getPositionAlongAxis(axis);

    return Double.compare(aVal, bVal);
  }
}
