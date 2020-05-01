package edu.brown.cs.teams.constants;

public class Constants {
    public static final String TRIE_DATA_PATH = "data/trie-data.txt";
    public static final String INGREDIENT_TERMS_PATH = "data/ingredient-terms.txt";
    public static final String DATABASE_FILE = "data/recipe.sqlite3";


    public static final String DB_HOST = "johnny.heliohost.org";
    public static final String DB_NAME = "bsmith28_wiyf_db";
    public static final String DB_PORT = "5432";
    public static final String DB_USERNAME = System.getenv("DB_USER"); //"bsmith28_ben";
    public static final String DB_PWD = System.getenv("DB_PWD"); //"wiyf1!";
    public static final String GOOGLE_CLIENT_ID = System.getenv("GOOGLE_ID"); // "727828985870-25d5atf1g5r853t5n6o90u0dansh9ao2.apps.googleusercontent.com";

}
