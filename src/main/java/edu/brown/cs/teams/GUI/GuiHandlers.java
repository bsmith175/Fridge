package edu.brown.cs.teams.GUI;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.io.Command;
import edu.brown.cs.teams.algorithms.RunKDAlg;
import edu.brown.cs.teams.algorithms.AlgMain;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.ingredientParse.IngredientSuggest;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.login.AccountUser;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//can have more objects for different types of handlers
public class GuiHandlers {

    private static final Gson GSON = new Gson();
    private static IngredientSuggest suggest;

    public GuiHandlers() throws Exception {
//        suggest = new IngredientSuggest();
//        String dbURL = "jdbc:postgresql://" + Constants.DB_HOST +
//            ":" + Constants.DB_PORT + "/" + Constants.DB_NAME;
//        AlgMain.setDb(new RecipeDatabase(dbURL, Constants.DB_USERNAME, Constants.DB_PWD, false));

    }
    public void setHandlers(FreeMarkerEngine freeMarker) {
        // Specify the algorithm to run here!!
        Command command = new RunKDAlg();
        Spark.get("/fridge", new FridgeHandler(), freeMarker);
        Spark.post("/recipe", new RecipeHandler());
        Spark.post("/recipe-recommend", new RecipeSuggestHandler(command));
    }
    //Handles a user login. Takes user data from the Google User and adds to the database if possible.
    private static class userLoginHandler implements Route {

        @Override
        public Object handle(Request request, Response response) throws Exception {
            QueryParamsMap qm = request.queryMap();
            String uid = qm.value("uid");
            String name = qm.value("firstName");
            String pfp = qm.value("profilePicture");
            AccountUser user = new AccountUser(uid, name, pfp);

            JsonObject responseJSON = new JsonObject();
            responseJSON.addProperty("uid", uid);
            responseJSON.addProperty("name", name);
            responseJSON.addProperty("profilePicture", pfp);

            try {
                AlgMain.getDb().addNewUser(user);
                responseJSON.addProperty("newUser", true);
            } catch (SQLException e) {
                responseJSON.addProperty("newUser", false);
                List<Integer> recipeIDs = AlgMain.getDb().getFavorites(uid);
            }

            return responseJSON.toString();
        }
    }

    //handles a request to the favorites page. Queries db for the user's favorited recipes.
    private static class favoritesPageHandler implements Route {

        @Override
        public Object handle(Request request, Response response) throws Exception {
            QueryParamsMap qm = request.queryMap();
            String uid = qm.value("uid");

            List<Integer> recipeIDs = AlgMain.getDb().getFavorites(uid);
            JsonArray responseJSON = new JsonArray();
            for (Integer curID : recipeIDs) {

                //this is where the json array is created
                JsonObject obj = AlgMain.getDb().getRecipeContentFromID(Integer.toString(curID));
                if (obj == null) {
                    throw new IllegalArgumentException("ERROR in favoritesHandler:  recipe doesn't exist");
                }
                responseJSON.add(obj);
            }
            return responseJSON.toString();
        }
    }

    //handles a user "favoriting" a recipe
    //returns JSON object with properties:
    //                          added - true, if recipe was added to favorites list
    //                                  false, if recipe was already in favorites list
    //                          error: true, if SQLException occured, false otherwise
    private static class favoriteButtonHandler implements Route {

        @Override
        public Object handle(Request request, Response response)  {
            QueryParamsMap qm = request.queryMap();
            String rid = qm.value("recipe_id");
            String uid = qm.value("user_id");
            JsonObject responseJSON = new JsonObject();
            try {
                if (AlgMain.getDb().addToFavorites(rid, uid)) {
                    responseJSON.addProperty("added", true);
                } else {
                    responseJSON.addProperty("added", false);
                }
            } catch (SQLException throwable) {
                responseJSON.addProperty("error", true);
                return responseJSON.toString();
            }
            responseJSON.addProperty("error", false);
            return responseJSON;
        }
    }

    private static class ingredientSuggestHandler implements Route {

        //returns a json list of ingredient strings, ordered from most relevant to least relevant
        @Override
        public Object handle(Request request, Response response) throws Exception {
            QueryParamsMap qm = request.queryMap();
            String input = qm.value("input");
            List<String> ingredients = suggest.suggest(input);
            String json = GSON.toJson(ingredients);
            return json;
        }
    }

    private static class RecipeSuggestHandler implements Route {
        private Command command;
        public RecipeSuggestHandler(Command command){
            this.command = command;
        }

        // Returns the suggested recipes
        @Override
        public Object handle(Request request, Response response) throws CommandException {
            QueryParamsMap qm = request.queryMap();
            String[] ingredients = qm.get("text").values();
            List<JsonObject> results = command.runForGui(ingredients);
            String result = GSON.toJson(results);
            return result;
        }
    }

    private static class FridgeHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("title",
                    "Fridge: Whats in Your Fridge", "message", "");
            return new ModelAndView(variables, "fridge.ftl");
        }
    }


    /**
     * A handler to produce our autocorrect service site.
     *
     * @return ModelAndView to render.
     * (autocorrect.ftl).
     */
    private static class RecipeHandler implements Route {
        @Override
        public String handle(Request req, Response res) throws ParseException {
            try (FileReader reader = new FileReader("data/smallJ.json")) {
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(reader);
                List<String> result = new ArrayList<>();

                result = new Gson().fromJson(String.valueOf(array), ArrayList.class);
                Map<String, Object> variables = ImmutableMap.of("results", result);
                return GSON.toJson(variables);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
