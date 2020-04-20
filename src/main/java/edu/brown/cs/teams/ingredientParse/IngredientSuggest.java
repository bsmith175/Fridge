package edu.brown.cs.teams.ingredientParse;

import edu.brown.cs.teams.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Public endpoint for getting ingredient suggestions.
 */
public class IngredientSuggest {
    private AutoCorrect ac;
    private Stubs stub;
    private HashMap<String, List<String>> termToIngredients;

    /**
     * constructs and IngredientSuggest object. Currently uses Stub class to
     * mimic database package.
     * @param filepath - filepath to .txt file used to construct Trie
     */
    public IngredientSuggest(String filepath) {
        ac = new AutoCorrect(filepath, true, false);
        stub = new Stubs();
    }

    /**
     * Matches user input to closest valid ingredients.
     * @param input - input from user ingredients search
     * @return - A list of ingredient tokens (eg "white rice")
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

        PriorityQueue<Pair<String, Integer>> pq = StringMatch.findClosest(correctedTokens, stub.getTermIngredientsMap());
        while (!pq.isEmpty()) {
            ret.add(pq.poll().getFirst());
        }

        return ret;
    }
}
