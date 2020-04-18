package src.main.java.edu.brown.cs.teams.ingredientParse;

import src.main.java.edu.brown.cs.teams.utils.Pair;

import java.util.*;

 class StringMatch {
    public static PriorityQueue<Pair<String, Integer>> findClosest(List<String> tokens, HashMap<String, Set<String>> map) {
        PriorityQueue<Pair<String, Integer>> ingredientCount;
        ingredientCount = new PriorityQueue<>(new IngredientPairComparator());

        HashMap<String, Integer> ingredientCountMap = new HashMap<String, Integer>();

        for (String token: tokens) {
            Set<String> ingredients = map.get(token);
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

        ingredientCountMap.forEach((k, v) -> ingredientCount.add(new Pair<String, Integer>(k, v)));
        return ingredientCount;
    }

    private static class IngredientPairComparator implements Comparator<Pair<String, Integer>> {

        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
            return o2.getSecond() - o1.getSecond();
        }
    }
}
