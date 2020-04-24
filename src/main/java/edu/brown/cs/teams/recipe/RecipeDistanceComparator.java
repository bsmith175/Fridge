package edu.brown.cs.teams.recipe;

import java.util.Comparator;

public class RecipeDistanceComparator implements Comparator<Recipe> {

  @Override
  public int compare(Recipe r1, Recipe r2) {
    return -Double.compare(r1.getSimilarity(), r2.getSimilarity());
  }
}
