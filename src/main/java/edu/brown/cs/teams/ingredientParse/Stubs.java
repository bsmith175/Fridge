package edu.brown.cs.teams.ingredientParse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        String[] ingredients = {"white rice", "brown rice", "rice", "rice noodles", "1 chicken stock cube",
                "1 tsp turmeric or a large pinch of saffron strands", "85g chorizo, diced",
                "200g pack mixed cooked seafood", "2 roasted red peppers", "spaghetti noodles",
                "100g frozen pea", "2 tbsp chopped parsley", "300g cooked rice noodles",
                "400g/14oz mixed vegetables", "140g cooked prawns",
                "100g cooked chicken or duck, shredded", "2 garlic cloves, finely chopped",
                "small piece ginger, finely chopped", "splash light soy sauce", "soy sauce",
                "Chinese five-spice powder, for sprinkling", " brik or filo pastry (see tips)",
                "1 egg, beaten", "sesame seeds, for sprinkling if you want", "100g reduced salt and sugar ketchup",
                "1 tbsp white wine vinegar", "small piece ginger, grated", "pinch of caster sugar"};

//        String filePath = "data/trie-data.txt";
//        List<String> asList = Arrays.asList(ingredients);
//        List<String> parsed = parseIngredients(asList);
//        try {
//            writeTrieData(parsed, filePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //termIngredientsMap = createTermMap(parsed);
    }


    //Takes in a list of parsed ingredients (eg "brown rice", "green chili" and creates
    //a map of each term in any ingredient ("brown", "rice", "green", "chili") to a list
    //of full ingredient names that contain the term.



}
