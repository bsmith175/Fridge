package edu.brown.cs.teams.GUI;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.algorithms.RecipeSuggest;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.algorithms.RunKDAlg;
import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.ingredientParse.IngredientSuggest;
import edu.brown.cs.teams.login.AccountUser;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.QueryParamsMap;
import spark.template.freemarker.FreeMarkerEngine;
import spark.TemplateViewRoute;
import spark.ModelAndView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.util.Map;

/**
 * Contains endpoints for communication with frontend.
 */
public class GuiHandlers {

  private static final Gson GSON = new Gson();
  private static IngredientSuggest suggest;
  private static GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
          .Builder(new NetHttpTransport(), new JacksonFactory())
          .setAudience(Collections.singletonList(Constants.GOOGLE_CLIENT_ID))
          .build();
  private static RunKDAlg favoritesSuggest;

  /**
   * Constructs a GuiHandler. Initialized the IngredientSuggest used for ingredient auto-suggest,
   * and the algorithm used to search KDTree.
   *
   * @throws Exception - If exception occurs during initialization
   */
  public GuiHandlers() throws Exception {
    suggest = new IngredientSuggest();
    favoritesSuggest = new RunKDAlg();

  }

  /**
   * Sets endpoints for client-server communication.
   *
   * @param freeMarker - Freemarker engine
   */
  public void setHandlers(FreeMarkerEngine freeMarker) {
    // Specify the algorithm to run here!!

    Spark.get("/", new FridgeHandler(), freeMarker);
    Spark.get("/home", new HomeHandler(), freeMarker);


    Spark.post("/suggested-recipes", new SuggestedHandler());
    Spark.post("/suggest", new IngredientSuggestHandler());

    Spark.post("/recipe-recommend",
            new RecipeSuggestHandler());
    Spark.post("/favorites", new FavoritesPageHandler());
    Spark.post("/heart", new FavoriteButtonHandler());
    Spark.post("/login", new UserLoginHandler());

    Spark.post("/pantry", new PantryHandler());
    Spark.post("/add-pantry", new PantryAddHandler());
    Spark.post("/remove-pantry", new RemovePantryHandler());
  }


  /**
   * Handler class to log a User in.
   * Takes user data from the Google User and adds to the database
   */
  private static class UserLoginHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
      //parameters from post request
      QueryParamsMap qm = request.queryMap();
      String tokenString = qm.value("idToken");

      JsonObject responseJSON = new JsonObject();

      //Verify google user validity with GoogleTokenVerifier
      GoogleIdToken idToken = verifier.verify(tokenString);
      if (idToken != null) {
        //if user is valid, get user information
        Payload payload = idToken.getPayload();
        String uid = payload.getSubject();
        String name = (String) payload.get("name");
        String pfp = (String) payload.get("picture");
        AccountUser user = new AccountUser(uid, name, pfp);

        //Add post parameters
        responseJSON.addProperty("uid", uid);
        responseJSON.addProperty("name", name);
        responseJSON.addProperty("profilePicture", pfp);

        try {
          AlgUtils.getUserDb().addNewUser(user);
          responseJSON.addProperty("newUser", true);
        } catch (SQLException e) {
          responseJSON.addProperty("newUser", false);
        }

        responseJSON.addProperty("success", true);

      } else {
        //if user is not valid, set success parameter false
        responseJSON.addProperty("success", false);

      }
      return responseJSON.toString();
    }
  }

  /**
   * Handles a request to the favorites page.
   * Queries user database for the user's/ favorited recipes.
   */
  private static class FavoritesPageHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String uid = qm.value("uid");

      try {
        //get recipe IDs of user's favorite recipes
        List<Integer> recipeIDs = AlgUtils.getUserDb().getFavorites(uid);

        JsonArray responseJSON = new JsonArray();
        for (Integer curID : recipeIDs) {

          //Gets JsonObject of recipe
          JsonObject obj;
          obj = AlgUtils.getRecipeDb().getRecipeContentFromID(curID);
          if (obj == null) {
            throw new IllegalArgumentException(
                    "ERROR in favoritesHandler:  recipe doesn't exist");
          }
          responseJSON.add(obj);
        }
        return responseJSON.toString();
      } catch (Exception e) {
        return "error";
      }
    }
  }

  //handles a user "favoriting" a recipe
  //returns JSON object with properties:
  //                          added - true, if recipe was added to favorites list
  //                                  false, if recipe was already in favorites list
  //                          error: true, if SQLException occured, false otherwise

  /**
   * handles a user "favoriting" a recipe.
   * returns JSON object with properties:
   * added - true, if recipe was added to favorites list
   * false, if recipe was already in favorites list
   * error: true, if SQLException occured, false otherwise.
   */
  private static class FavoriteButtonHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int rid = Integer.parseInt(qm.value("recipe_id"));
      String uid = qm.value("user_id");
      JsonObject responseJSON = new JsonObject();

      //attempt to add favorite to database
      try {
        if (AlgUtils.getUserDb().addToFavorites(rid, uid)) {
          //new favorite was successfuly added
          responseJSON.addProperty("added", true);
        } else {
          //if recipe was a favorite, remove from user's favorites
          AlgUtils.getUserDb().removeFavorite(rid, uid);
          responseJSON.addProperty("added", false);
        }
      } catch (SQLException throwable) {
        //error occured while interacting with database
        responseJSON.addProperty("error", true);
        return responseJSON.toString();
      }
      responseJSON.addProperty("error", false);
      return responseJSON;
    }
  }

  /**
   * Recipe results for Input ingredients.
   */
  private static class IngredientSuggestHandler implements Route {

    //returns a json list of ingredient strings, ordered from most relevant to least relevant
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String input = qm.value("input");
      List<String> ingredients = suggest.suggest(input);
      return GSON.toJson(ingredients);
    }
  }

  /**
   *
   */
  private static class SuggestedHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String uid = qm.get("uid").values()[0];
      String result;
      try {
        result = GSON.toJson(favoritesSuggest.getRecommendations(uid));
      } catch (Exception e) {
        return "error";
      }
      return result;
    }
  }
  /**
   * Recipe results for Input ingredients.
   * Adds restriction groups to results.
   */
  private static class RecipeSuggestHandler implements Route {

    // Returns the suggested recipes
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String[] ingredients = qm.get("text").values();
      boolean meats = Boolean.parseBoolean(qm.get("meats").value());
      boolean dairy = Boolean.parseBoolean(qm.get("dairy").value());
      boolean nuts = Boolean.parseBoolean(qm.get("nuts").value());
      RecipeSuggest suggestor = new RecipeSuggest(meats, dairy, nuts);
      String result;
      List<JsonObject> results;
      try {
        results = suggestor.runForGui(ingredients);
        result = GSON.toJson(results);
      } catch (Exception e) {
        result = "none";
      }
      return result;
    }
  }
  /**
   * Renders fridge.ftl, sets google id.
   */
  private static class FridgeHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
              "Fridge: Whats in Your Fridge", "message", "",
              "google_client_id", Constants.GOOGLE_CLIENT_ID);
      return new ModelAndView(variables, "fridge.ftl");
    }
  }

  /**
   * renders home page, sets google id.
   */
  private static class HomeHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
              "Fridge: Whats in Your Fridge", "message", "",
              "google_client_id", Constants.GOOGLE_CLIENT_ID);
      return new ModelAndView(variables, "home.ftl");
    }
  }



  /**
   * Handler for adding a list of ingredients to the pantry.
   */
  private static class PantryAddHandler implements Route {

    //Takes in array of ingredients entered by user, just like for a recipe search.
    //additionally has another parameter, "uid", which is the user's ID.
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String uid = qm.value("uid");
      String term = qm.value("text");
      JsonObject responseJSON = new JsonObject();

      try {
        AlgUtils.getUserDb().addToPantry(term, uid);
        responseJSON.addProperty("success", true);
      } catch (Exception e) {
        responseJSON.addProperty("success", false);
      }

      return responseJSON.toString();
    }
  }


  /**
   * removes an ingredient from user's pantry.
   * Takes in parameters:
   * "text" - the ingredient to remove
   * "uid" - the user ID.
   */
  private static class RemovePantryHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String term = qm.value("text");
      String uid = qm.value("uid");
      JsonObject responseJSON = new JsonObject();

      try {
        AlgUtils.getUserDb().removePantryitem(term, uid);
        responseJSON.addProperty("success", true);

      } catch (SQLException e) {
        responseJSON.addProperty("success", false);
      }
      return responseJSON.toString();
    }
  }


  /**
   * returns a list of ingredient names in the user's pantry.
   * Takes in one parameter:
   * "uid" - the user ID
   */
  private static class PantryHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String uid = qm.value("uid");
      String responseJSON = "";
      try {
        responseJSON = GSON.toJson(AlgUtils.getUserDb().getPantry(uid));
        return responseJSON;
      } catch (SQLException e) {
        return responseJSON;
      }
    }
  }

}
