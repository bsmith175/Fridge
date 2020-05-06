//package edu.brown.cs.teams.test;
//
//
//import edu.brown.cs.teams.ingredientParse.IngredientSuggest;
//import edu.brown.cs.teams.ingredientParse.ledComparator;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import java.util.List;
//
//public class ingredientParseTest {
//  static IngredientSuggest ig;
//
//  @BeforeClass
//  public static void setUp() {
//
//      try {
//          ig = new IngredientSuggest();
//      } catch (Exception e) {
//          e.printStackTrace();
//          System.exit(1);
//      }
//
//  }
//
//  @Test
//  public void test() {
//    List<String> output = ig.suggest("chicken");
//    assert (output.contains("chicken"));
//    assert (output.contains("chicken stock"));
//    assert (output.contains("chicken livers"));
//    assert (output.contains("chicken thigh"));
//
//    // prefix
//    List<String> chicke = ig.suggest("chicke");
//    assert (chicke.equals(output));
//
//    // suggestions with input not as first word
//    output = ig.suggest("noo");
//    assert (output.contains("rice noodles"));
//    assert (output.contains("egg noodles"));
//
//    // suggestions are ranked by led closeness
//    output = ig.suggest("chick");
//    assert (output.get(0).equals("chicken"));
////    assert (output.get(1).equals("chickens"));
////    assert (output.get(2).equals("chickpea"));
////    assert (output.get(3).equals("chickpeas"));
//
//    //no results
//    output = ig.suggest("fridge");
//    assert (output.size() == 0);
//
//
//  }
//
//  @Test
//    public void ledComparatorTest() {
//      ledComparator comp= new ledComparator();
//      comp.setDest("testing");
//
//      assert (comp.compare("testing", "testin") < 0);
//      assert (comp.compare("texting", "tezzing") < 0);
//      assert (comp.compare("test", "") < 0);
//
//      assert (comp.compare("testin", "testing") > 0);
//      assert (comp.compare("texxing", "texting") > 0);
//
//      assert (comp.compare("aa", "f") == 0);
//      assert (comp.compare("nim", "nim") == 0);
//      comp.setDest("another");
//      assert (comp.compare("", "") == 0);
//      assert (comp.compare("xtesting", "testingx") == 0);
//
//  }
//}
//
