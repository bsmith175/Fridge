package src.main.java.edu.brown.cs.teams.recipe;

import java.util.HashMap;
import java.util.Map;

public class Ingredient {
  private String id;
  private String category;
  private double[] vector;
  private double distance;


  public Ingredient(String id, double[] vector) {
    this.id = id;
    this.vector = vector;
  }

  public String getCategory() {
    return this.category;
  }

  public double[] getVec() {
    return vector;
  }

  public String getId() {
    return this.id;
  }

  /**
   * Gets the distance of this ingredient to a nearest ingredient. Useful for
   * the recipe class to not have to repeat calculations.
   * @return the distance double
   */
  public double getDistance() {
    return this.distance;
  }

  /**
   * Sets the distance of this ingredient to a nearest ingredient. Useful for
   * the recipe class to not have to repeat calculations.
   * @param distance
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }
}
