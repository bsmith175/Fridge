package edu.brown.cs.teams.GUI;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.teams.login.AccountUser;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.List;

//can have more objects for different types of handlers
public class GuiHandlers {

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
                StubAlgMain.getDB().addNewUser(user);
                responseJSON.addProperty("newUser", false);
            } catch (SQLException e) {
                responseJSON.addProperty("newUser", true);
            }

            return responseJSON;
        }
    }

    private static class favoritesHandler implements Route {


        @Override
        public Object handle(Request request, Response response) throws Exception {
            QueryParamsMap qm = request.queryMap();
            String uid = qm.value("uid");

            List<Integer> recipeIDs = StubAlgMain.getDB().getFavorites(uid);
            JsonArray responseJSON = new JsonArray();
            for (Integer curID : recipeIDs) {
                JsonObject obj = StubAlgMain.getDB().getRecipeFromID(Integer.toString(curID));
                responseJSON.add(obj);
            }
            return responseJSON;
        }
    }
}
