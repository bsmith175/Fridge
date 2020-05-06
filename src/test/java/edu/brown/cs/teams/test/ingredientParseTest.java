package edu.brown.cs.teams.test;


import edu.brown.cs.teams.database.Tokenizer;
import edu.brown.cs.teams.ingredientParse.IngredientSuggest;
import edu.brown.cs.teams.ingredientParse.LedComparator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ingredientParseTest {
  static IngredientSuggest ig;

  @BeforeClass
  public static void setUp() {

      try {
          ig = new IngredientSuggest();
      } catch (Exception e) {
          e.printStackTrace();
          System.exit(1);
      }

  }

  @Test
  public void testSuggest() {
    List<String> output = ig.suggest("chicken");
    assert (output.contains("chicken"));
    assert (output.contains("chicken stock"));
    assert (output.contains("chicken livers"));
    assert (output.contains("chicken thigh"));
    // prefix
    List<String> chicke = ig.suggest("chicke");
    assert (chicke.equals(output));
    // suggestions with input not as first word
    output = ig.suggest("noo");
    assert (output.contains("rice noodles"));
    assert (output.contains("egg noodles"));

    //autocomplete prefix
    assert (output.contains("noodle"));

    //ranking of results
    assert (output.indexOf("noodle") < output.indexOf("noodles"));

    // suggestions are ranked by led closeness
    output = ig.suggest("chick");
    assert (output.get(0).equals("chicken"));
    assert (output.get(1).equals("chickens") || output.get(1).equals("chickpea"));
    assert (output.get(2).equals("chickens") || output.get(1).equals("chickpea"));
    assert (output.get(3).equals("chicken kg") || output.get(3).equals(
            "chickpeas"));

    //no results
    output = ig.suggest("fridge");
    assert (output.size() == 0);


  }

  @Test
    public void ledComparatorTest() {
      LedComparator comp= new LedComparator();
      comp.setDest("testing");

      assert (comp.compare("testing", "testin") < 0);
      assert (comp.compare("texting", "tezzing") < 0);
      assert (comp.compare("test", "") < 0);

      assert (comp.compare("testin", "testing") > 0);
      assert (comp.compare("texxing", "texting") > 0);

      assert (comp.compare("aa", "f") == 0);
      assert (comp.compare("nim", "nim") == 0);
      comp.setDest("another");
      assert (comp.compare("", "") == 0);
      assert (comp.compare("xtesting", "testingx") == 0);

  }

  @Test
  public void tokenizerTest() {
    Tokenizer tokenizer = new Tokenizer();
    List<String> ingreds = new ArrayList<String>();
    ingreds.add("32g 15 horseradish");
    ingreds.add("15 pounds pound oz g kg lb tbsp tsp c cups teaspoon   crab meat");
    ingreds.add("kale or collard greens");
    ingreds.add("lettuce (fresh preferred)");
    ingreds.add("beef \\u09");
    ingreds.add("a x big fish");
    ingreds.add("noodles tbsp");
    ingreds.add("medium-rare steak");
    ingreds.add("a a a salmon kg and ");
    List<String> results = tokenizer.parseIngredients(ingreds);
    //removes word with numbers
    assert (results.contains("horseradish"));
    assert (!results.contains("32g 15 horseradish"));
    //removes measurement words, singular and plural, and extra spaces
    assert (results.contains("crab meat"));
    assert (!results.contains("15 pounds pound oz g kg lb tbsp tsp c cups teaspoon   crab meat"));
    //removes anything after "or"
    assert (results.contains("kale"));
    assert (!results.contains("kale or collard greens"));
    //removes anything inside parentheses
    assert (results.contains("lettuce"));
    assert (!results.contains("lettuce (fresh preferred)"));
    //removes words starting with unicode escape
    assert (results.contains("beef"));
    assert (!results.contains("beef \\u09"));
    //removes single characters and the word big
    assert (results.contains("fish"));
    assert (!results.contains("a x big fish"));
    //removes regex at end of string (not space surrounded)
    assert (results.contains("noodles"));
    assert (!results.contains("noodles tbsp"));
    assert (results.contains("steak"));
    assert (!results.contains("medium-rare steak"));
    //test matches sharing space get removed, and keyword in the middle stays
    assert (results.contains("salmon"));
    assert (!results.contains("a a a salmon kg and "));
  }
}


