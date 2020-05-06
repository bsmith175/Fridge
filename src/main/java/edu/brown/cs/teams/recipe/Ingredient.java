package edu.brown.cs.teams.recipe;

import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.kdtree.CartesianPoint;
import java.util.List;

/**
 * class for an ingredient.
 */
public class Ingredient extends CartesianPoint {
  private String id;

  /**
   * constructor for an ingredient.
   *
   * @param id the ingredient id
   */
  public Ingredient(String id) {
    super(new double[]{});
    this.id = id;
  }

  /**
   * gets the vector from the word embedding map.
   *
   * @return the word embedding (double[])
   */
  @Override
  public double[] getPosition() {
    return AlgUtils.getVectorMap().get(id);
  }

  /**
   * gets the id of the ingredient.
   *
   * @return id of this ingredient
   */
  public String getId() {
    return this.id;
  }


  /**
   * Gives the most likely candidate to represent a recipe ingredient in
   * the user ingredient list.
   *
   * @param ingredients the user ingredient list
   * @return the closest vector ingredient to the recipe ingredient
   */
  public Ingredient generateCandidate(
          List<Ingredient> ingredients) {
    int num = ingredients.size();
    Ingredient best = null;
    double closest = 0.0;
    for (Ingredient i : ingredients) {
      double similarity = this.getDistance(i.getPosition());
      if (similarity > closest) {
        best = i;
        closest = similarity;
      }
    }
    if (closest > AlgUtils.SIMILARITY_THRESHOLD - num * AlgUtils.SIMILARITY_FACTOR) {
      return best;
    } else {
      return null;
    }
  }

  /**
   * equals method, used for testing.
   * @param ingredient the ingredient compared to
   * @return true if the ids are the same
   */
  public boolean equals(Ingredient ingredient) {
    return id.equals(ingredient.getId());
  }


}
