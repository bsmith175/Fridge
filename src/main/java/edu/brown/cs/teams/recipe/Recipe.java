package src.main.java.edu.brown.cs.teams.recipe;

import src.main.java.edu.brown.cs.teams.kdtree.CartesianPoint;
import src.main.java.edu.brown.cs.teams.state.Config;

import java.util.*;

public class Recipe extends CartesianPoint {
  private String id;
  private Set<Ingredient> ingredients;
  private double similarity;

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
  }

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
   * @return the vector of concatenated ingredient vectors in this recipe
   */
  public double[] genRecipeVec() {
    double[] recipeVec = new double[]{Config.getEmbedLength()};
    for (int dim = 0; dim < Config.getEmbedLength(); dim++) {
      for (Ingredient ingr : this.ingredients)
      recipeVec[dim] = recipeVec[dim] + ingr.getVec()[dim];
    }
    return recipeVec;
  }

  /**
   * Generates the closest list of ingredients to a recipe from an ingredient
   * list.
   * @param ingredients a user list of ingredients
   * @return an approximation of the recipe within the user ingredients
   */
  public List<Ingredient> compareToIngredients(
          List<Ingredient> ingredients) {
    double[] candidatesVec = new double[]{this.ingredients.size()};
    List<Ingredient> candidates = new ArrayList<>();
    for (Ingredient ing : this.ingredients) {
      Ingredient candidate = Config.generateCandidate(ingredients, ing);
      candidatesVec = Config.arrayAdd(new double[][]{candidatesVec, candidate.getVec()});
      candidates.add(candidate);
    }
    this.similarity = this.getDistance(candidatesVec);
    return candidates;
  }


  /**
   * gets the similarity score of this recipe
   * @return the double score
   */
  public double getSimilarity() {
    return this.similarity;
  }
}
