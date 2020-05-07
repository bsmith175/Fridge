package edu.brown.cs.teams.recipe;

import java.util.Comparator;
import java.util.Map;

/**
 * class to compare map values by similarity for recipes.
 */
public class RecipeDistanceComparator implements Comparator<Integer> {
  private Map<Integer, Double> map;

  /**
   * constructor for recipe distance comparator.
   * @param simMap recipe id to double similarity map.
   */
  public RecipeDistanceComparator(Map<Integer, Double> simMap) {
    this.map = simMap;
  }

  /**
   * compare two <id, similarity> entries.
   * @param r1 first recipe id
   * @param r2 second recipe id
   * @return the result of comparison
   */
  @Override
  public int compare(Integer r1, Integer r2) {
    return -Double.compare(map.get(r1), map.get(r2));
  }
}
