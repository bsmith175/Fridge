package src.main.java.edu.brown.cs.teams.main;

import src.main.java.edu.brown.cs.teams.ingredientParse.IngredientSuggest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

public final class Main {

    public static void main(String[] args) {
        IngredientSuggest ig = new IngredientSuggest("data/ingredients.txt");

        PrintWriter pw = new PrintWriter(System.out);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";

        pw.flush();
        try {
            input = reader.readLine();

        } catch (IOException e) {
            pw.println("ERROR: error reading input");
        }

        // repl loop
        while (input != null) {
            List<String> ingredients = ig.suggest(input);
            for (String ingredient : ingredients) {
                pw.println(ingredient);
            pw.flush();
            try {
                input = reader.readLine();

            } catch (IOException e) {
                pw.println("ERROR: error reading input");
            }

        }
        pw.close();

    }
    }

}
