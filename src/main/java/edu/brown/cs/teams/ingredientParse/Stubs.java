package src.main.java.edu.brown.cs.teams.ingredientParse;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * Temporary class to use before database classes are finished
 */
 class Stubs {
    private HashMap<String, Set<String>> termIngredientsMap;

    public HashMap<String, Set<String>> getTermIngredientsMap() {
        return termIngredientsMap;
    }

    public Stubs() {
        String[] ingredients = {"350g short pasta shapes (we used orzo )", "1 chicken stock cube",
                "1 tsp turmeric or a large pinch of saffron strands", "85g chorizo, diced",
                "200g pack mixed cooked seafood", "2 roasted red peppers,from a jar, sliced",
                "100g frozen pea", "2 tbsp chopped parsley"};
        List<String> asList = Arrays.asList(ingredients);
        List<String> parsed = parseIngredients(asList);
        termIngredientsMap = createTermMap(parsed);
    }
    /**
     * Parses ingredients into ingredient keywords.
     * @param raw - List of ingredient text from recipe
     * @return - The inputted list with all non-keywords removed
     */
    private List<String> parseIngredients(List<String> raw) {
        //initialize return list
        List<String> ret = new ArrayList<String>();

        //regex matches any amount of whitespace
        String whiteSpace = "\\s+";

        //regex matches all non-keywords
        String regex = "[(][\\w\\W]*[)]|[,][\\w\\W]*|\\s+[Gg]{1}\\s+|\\s+[Kk]{1}[Gg]{1}\\s+|\\s+[Mm]{1}[Ll]{1}\\s+|\\s+[Pp][Tt]\\s+|[Pp]int[s]?|[Mm]illiliter[s]?|[Kk]ilogram[s]?|[Tt]ablespoon[s]?|[Pp]inch(es)?|[Dd]ash|\\d+[\\w]*|[Tt]easpoon[s]?|[Tt]ablespoon[s]?|[Jj]igger[s]?|[Dd]ash(es)?|\\s+[Tt]{1}[Bb]{1}[Ss]{1}[Pp]{1}[s]*|\\s+[Oo][Zz]\\s+|\\s+[Qq][Tt]\\s+|\\s+[Cc]\\s+|\\s+[Ff][Ll]\\s+|\\s+[Tt]\\s+|\\s+[Cc][Mm]\\s+|[Aa]bout|\\s+[Aa]\\s+|\\s+[Oo][f]\\s+|\\s+[Oo][r][\\w\\W]+|\\s+[Aa]nd\\s+|\\s+[Tt]hinly\\s+|\\s+[Hh]alf\\s+|\\s+[Hh]alve[\\w]*\\s+|\\s+[Pp]iece\\s+|[Hh]andful[\\w]*\\s+|[Tt]humb-[\\w]*\\s+|[Ww]hole[\\w]*\\s+|[Ff]ine[\\w]*\\s+|[Mm]ini[\\w]*\\s+|[Bb]unch[\\w]*\\s+|[Jj]ar[\\w]*\\s+|[Bb]ars[\\w]*\\s+|[Rr]ounded[\\w]*\\s+|[Ff]resh[\\w]*\\s+|[Pp]ack[\\w]*\\s+|[Ss]tone[\\w]*\\s+|[Tt]ub[s]*\\s+|[Cc]hop[\\w]*\\s+|[Ss]oft[\\w]*\\s+|[Tt]oasted[\\w]*\\s+|[Ss]prig[\\w]*\\s+|[Hh]eaped[\\w]*\\s+|[Ff]istful[\\w]*\\s+|\\s+[Pp]ot[\\w]*\\s+|[\\\\]{1}[^\\s]*|[Ll]arge[\\w]*\\s+|[Mm]edium[^\\s]*|[Ss]mall[^\\s]*|[Nn]atural[^\\s]*|[Ff]resh[^\\s]*|[Hh]ead[\\w]*|[^\\s]+ly|[Ss]lice[^\\s^,]*|[Tt]{1}[Ss]{1}[Pp]{1}[s]*|[Cc]an[s]*\\s+|[Ll]ittle[\\s]+|[^\\s][Ff]or[^\\s]|\\s+[Tt]he\\s+|[\\/]+[^\\s]*|[-]\\s+|[Cc]arton[s]?\\s+|[.]+";

        for (String unparsed : raw) {
            //replace all non-keywords with a space (to prevent keywords from concatenating)
            // and ensure only single space between keywords
            ret.add(unparsed.replaceAll(regex, " ").replaceAll(whiteSpace, " ").trim());
        }
        return ret;
    }

    //Takes in a list of parsed ingredients (eg "brown rice", "green chili" and creates)
    //a map of each term in any ingredient ("brown", "rice", "green", "chili") to a list
    //of full ingredient names that contain the term.
    private HashMap<String, Set<String>> createTermMap(List<String> ingredients) {
        HashMap<String, Set<String>> ret = new HashMap<String, Set<String>>();

        for (String ingredient: ingredients) {
            String[] terms = ingredient.split("\\s+");
            for (String term : terms) {
                if (ret.containsKey(term.trim())) {
                    ret.get(term.trim()).add(ingredient);
                } else {
                    Set<String> newSet = new HashSet<String>();
                    newSet.add(ingredient.trim());
                    ret.put(term.trim(), newSet);
                }
            }
        }
        return ret;
    }
}
