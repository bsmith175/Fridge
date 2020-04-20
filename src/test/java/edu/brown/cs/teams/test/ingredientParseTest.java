package edu.brown.cs.teams.test;

import edu.brown.cs.teams.ingredientParse.IngredientSuggest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ingredientParseTest {
    static IngredientSuggest ig;

    @BeforeClass
    public static void setUp() {
         ig = new IngredientSuggest("data/trie-data.txt");

    }
    @Test
    public void test() {
       List<String> output = ig.suggest("chicken");
       assert(output.contains("cooked chicken"));
       assert(output.contains("chicken stock cube"));
       assert(output.size() == 2);
       List<String> ch = ig.suggest("ch");
       assert(ch.equals(output));

       output = ig.suggest("noo");
       assert(output.contains("cooked rice noodles"));
       assert(output.contains("spaghetti noodles"));
       assert(output.contains("rice noodles"));
       assert(output.size() == 3);

       output = ig.suggest("rice noo");
       assert(output.get(0).equals("rice noodles"));
       assert(output.get(1).equals("cooked rice noodles"));
       assert(output.get(2).equals("rice"));

    }
}
