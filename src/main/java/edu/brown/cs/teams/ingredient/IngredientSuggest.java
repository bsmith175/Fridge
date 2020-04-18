package src.main.java.edu.brown.cs.teams.ingredient;

import src.main.java.edu.brown.cs.teams.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class IngredientSuggest {
    private AutoCorrect ac;
    private Stubs stub;
    private HashMap<String, List<String>> termToIngredients;
    public IngredientSuggest(String filepath) {
        ac = new AutoCorrect(filepath, true, false);
        Stubs stub = new Stubs();

    }
    public List<String> suggest(String input) {
        String[] tokens = input.split(" ");
        List<String> correctedTokens = new ArrayList<String>();
        for (String token : tokens) {
            correctedTokens.add(ac.suggest(token).peek());
        }

        List<String> ret = new ArrayList();

        PriorityQueue<Pair<String, Integer>> pq = StringMatch.findClosest(correctedTokens, stub.getTermIngredientsMap());
        while (!pq.isEmpty()) {
            ret.add(pq.poll().getFirst());
        }

        return ret;
    }
}
