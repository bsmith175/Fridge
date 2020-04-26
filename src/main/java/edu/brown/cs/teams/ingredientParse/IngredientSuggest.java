package edu.brown.cs.teams.ingredientParse;

import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.utils.Pair;

import java.io.*;
import java.util.*;

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
     * @param input - input from user ingredients search
     * @return - A list of ingredient tokens (eg "white rice")
     *          null if no ingredient matched the input
     */
    public List<String> suggest(String input) {
        if ( input.equals("")) {
            return new ArrayList<>();
        }
        String[] tokens = input.split(" ");
        List<String> correctedTokens = new ArrayList<String>();
        for (String token : tokens) {
            correctedTokens.add(ac.suggest(token).peek());
        }

        if (correctedTokens.size() == 0) {
            return null;
        }
        List<String> ret = new ArrayList();

        PriorityQueue<Pair<String, Integer>> pq = StringMatch.findClosest(correctedTokens, termToIngredients);
        while (!pq.isEmpty()) {
            ret.add(pq.poll().getFirst());
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

        HashMap<String, Set<String>> ret = new HashMap<String, Set<String>>();

        for (String ingredient: ingredients) {
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
}
