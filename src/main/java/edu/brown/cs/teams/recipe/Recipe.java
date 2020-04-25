package edu.brown.cs.teams.recipe;

import edu.brown.cs.teams.kdtree.CartesianPoint;
import edu.brown.cs.teams.algorithms.Config;

import java.util.*;

public class Recipe extends CartesianPoint {
  private String id;
  private Set<Ingredient> ingredients;
  private double similarity;
  private double[] recipeVec;

  /**
   * Constructor for Cartesian point.
   *
   * @param embedding double array storing position of point
   * @param id        a String
   */
  public Recipe(double[] embedding, String id, Set<Ingredient> ingredients) {
    super(embedding);
    this.id = id;
    this.ingredients = ingredients;
    this.similarity = 0.0;
    this.genRecipeVec();
  }

  /**
   * getter method for ingredients
   * @return the set of ingredients in the recipe
   */
  public Set<Ingredient> getIngredients() {
    return this.ingredients;
  }

  /**
   * getter method for id
   * @return the set of ingredients in the recipe
   */
  public String getId() {
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

  /**
   * Makes a vector for this recipe.
   *
   * @return the vector of concatenated ingredient vectors in this recipe
   */
  public void genRecipeVec() {
    this.recipeVec = Config.ingredAdd(this.ingredients);
  }

  /**
   * Generates the closest list of ingredients to a recipe from an ingredient
   * list.
   *
   * @param ingredients a user list of ingredients
   * @return an approximation of the recipe within the user ingredients
   */
  public List<Ingredient> compareToIngredients(
          List<Ingredient> ingredients) {
    List<Ingredient> candidateList = new ArrayList<>();
    //generate user candidate for every recipe ingredient
    for (Ingredient ing : this.ingredients) {
      Ingredient candidate = Config.generateCandidate(ingredients, ing);
      if (candidate != null) {
        candidateList.add(candidate);
      }
    }
    //approxmating a reicpe vector from user candidate ingredients
    double[] candidatesVec = Config.ingredAdd(candidateList);
    //distance from recipe to user approximated recipe
    double distance = 0.0;
    if (candidateList.size() != 0) {
      distance = Config.cosineSimilarity(this.recipeVec,
              candidatesVec);
    }
    //penalizing based on number of missing ingredients
    this.similarity =
            distance *
                    ((this.recipeVec.length - candidatesVec.length) * 0.15 + 1);
    return candidateList;
  }


  /**
   * gets the similarity score of this recipe
   *
   * @return the double score
   */
  public double getSimilarity() {
    return this.similarity;
  }
}
