package edu.brown.cs.teams.GUI;

import edu.brown.cs.teams.database.UserDatabase;
import edu.brown.cs.teams.ingredientParse.IngredientSuggest;

/**
 * temporary class to take the place of the global static class that eyal made.
 */
public class StubAlgMain {
    private static UserDatabase db;
    private static IngredientSuggest suggest;

    public static UserDatabase getDB() {
        return db;
    }


    public static IngredientSuggest getIngredientSuggest() {
        return suggest;
    }
}
