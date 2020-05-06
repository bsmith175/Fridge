package edu.brown.cs.teams.ingredientParse;

import edu.brown.cs.teams.constants.Constants;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Comparator;

/**
 * Public endpoint for getting ingredient suggestions.
 */
public class IngredientSuggest {
  private AutoCorrect ac;
  private HashMap<String, Set<String>> termToIngredients;

  /**
   * constructs and IngredientSuggest object. Currently uses Stub class to
   * mimic database package.
   */
  public IngredientSuggest() throws Exception {
    ac = new AutoCorrect(Constants.TRIE_DATA_PATH, true, false);
    termToIngredients = createTermMap();
  }

  /**
   * Matches user input to closest valid ingredients.
   *
   * @param input - input from user ingredients search
   * @return - A list of ingredient tokens (eg "white rice")
   * null if no ingredient matched the input
   */
  public List<String> suggest(String input) {
    if (input.equals("")) {
      return new ArrayList<>();
    }
    String[] tokens = input.split(" ");
    List<String> correctedTokens = new ArrayList<>();
    List<String> ret = new ArrayList<>();

    if (tokens.length == 1) {
      PriorityQueue<String> output = findAll(ac.suggest(tokens[0]), tokens[0]);
      while (!output.isEmpty()) {
        ret.add(output.poll());
      }
    } else {
      for (String token : tokens) {
        correctedTokens.add(ac.suggest(token).peek());
      }

      if (correctedTokens.size() == 0) {
        return null;
      }
      PriorityQueue<Pair<String, Integer>> pq = findClosest(correctedTokens);
      while (!pq.isEmpty()) {
        ret.add(pq.poll().getFirst());
      }
    }

    return ret;
  }

  private HashMap<String, Set<String>> createTermMap() throws Exception {
    File ingredientsFile = new File(Constants.INGREDIENT_TERMS_PATH);
    BufferedReader bf = new BufferedReader(new FileReader(ingredientsFile));
    List<String> ingredients = new ArrayList<>();
    String curLine;

    while ((curLine = bf.readLine()) != null) {
      ingredients.add(curLine);
    }

    HashMap<String, Set<String>> ret = new HashMap<>();

    for (String ingredient : ingredients) {
      String[] terms = ingredient.split("\\s+");
      for (String term : terms) {
        if (ret.containsKey(term.trim())) {
          ret.get(term.trim()).add(ingredient);
        } else {
          Set<String> newSet = new HashSet<>();
          newSet.add(ingredient.trim());
          ret.put(term.trim(), newSet);
        }
      }
    }
    return ret;
  }

  private PriorityQueue<Pair<String, Integer>> findClosest(
          List<String> tokens) {
    PriorityQueue<Pair<String, Integer>> ingredientCount =
            new PriorityQueue<>(new IngredientPairComparator());

    HashMap<String, Integer> ingredientCountMap =
            new HashMap<>();

    for (String token : tokens) {
      Set<String> ingredients = termToIngredients.get(token);
      if (ingredients != null) {
        for (String ingredient : ingredients) {
          Integer count = ingredientCountMap.get(ingredient);
          if (count == null) {
            count = 0;
          }
          count++;
          ingredientCountMap.put(ingredient, count);
        }
      }
    }

    ingredientCountMap.forEach((k, v) -> ingredientCount.add(new Pair<>(k, v)));
    return ingredientCount;
  }


  private PriorityQueue<String> findAll(PriorityQueue<String> tokens,
                                        String input) {
    String cur = tokens.poll();
    LedComparator lc = new LedComparator();
    lc.setDest(input);
    PriorityQueue<String> ret = new PriorityQueue<>(lc);
    while (cur != null) {
      Set<String> ingredients = termToIngredients.get(cur);
      if (ingredients != null) {
        ret.addAll(ingredients);
      }
      cur = tokens.poll();
    }
    return ret;
  }

  private static class IngredientPairComparator
          implements Comparator<Pair<String, Integer>> {

    @Override
    public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
      int indicator = o2.getSecond() - o1.getSecond();
      if (indicator == 0) {
        return o1.getFirst().length() - o2.getFirst().length();
      }
      return indicator;
    }
  }

}

