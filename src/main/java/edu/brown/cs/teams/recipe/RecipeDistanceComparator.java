package edu.brown.cs.teams.recipe;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RecipeDistanceComparator implements Comparator<Integer> {
  Map<Integer, Double> map;

  public RecipeDistanceComparator(Map<Integer, Double> simMap) {
    this.map = simMap;
  }

  @Override
  public int compare(Integer r1, Integer r2) {
    return -Double.compare(map.get(r1), map.get(r2));
  }
}
