package edu.brown.cs.teams.kdtree;


import java.util.Arrays;

/**
 * Abstract class that represents a point in space.
 */
public abstract class CartesianPoint implements DistanceMetric {
  private double[] position;

  /**
   * Constructor for Cartesian point.
   * @param position double array storing position of point
   */
  public CartesianPoint(double[] position) {
    this.position = position;
  }

  @Override
  public double getDistance(double[] target) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < getPosition().length; i++) {
      dotProduct += getPosition()[i] * target[i];
      normA += Math.pow(getPosition()[i], 2);
      normB += Math.pow(target[i], 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }

  /**
   * Getter for dimensionality of point.
   * @return number of dimensions of point
   */
  public int getNumDimensions() {
    return position.length;
  }

  /**
   * Getter for point position.
   * @return double array that stores point position
   */
  public double[] getPosition() {
    return this.position;
  }

  /**
   * Get coordinate of point along an axis.
   * @param axis to find position on
   * @return position on that axis
   */
  public double getPositionAlongAxis(int axis) {
    return this.position[axis];
  }

  /**
   * Returns the location of the point as a string.
   * @return String representing points location
   */
  public String toString() {
    return Arrays.toString(position);
  }

  /**
   * Determines if two points have the exact same position in space.
   * @param p another point to compare to
   * @return true if points have the same position, false otherwise.
   */
  public boolean isEqual(CartesianPoint p) {
    for (int i = 0; i < p.getNumDimensions(); i++) {
      if (this.getPositionAlongAxis(i) != p.getPositionAlongAxis(i)) {
        return false;
      }
    }
    return true;
  }
}
