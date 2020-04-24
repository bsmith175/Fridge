package edu.brown.cs.teams.main;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.ingredientParse.IngredientSuggest;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.json.JSONException;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

public final class Main {
  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();

  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("database");
    parser.accepts("ben");


    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
    if (options.has("database")) {
      RecipeDatabase r = null;
      try {
        r = new RecipeDatabase("data/recipe.sqlite3");
        r.makeTable();
        r.parseJson();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      }

    }
    if (options.has("ben")) {


      IngredientSuggest ig = new IngredientSuggest("data/trie-data.txt");

      PrintWriter pw = new PrintWriter(System.out);
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String input = "";

      pw.flush();
      try {
        input = reader.readLine();

      } catch (
              IOException e) {
        pw.println("ERROR: error reading input");
      }

      // repl loop
      while (input != null) {
        List<String> ingredients = ig.suggest(input);
        if (ingredients != null) {
          for (String ingredient : ingredients) {
            pw.println(ingredient);
            pw.flush();
          }
        }
        try {
          input = reader.readLine();

        } catch (IOException e) {
          pw.println("ERROR: error reading input");
        }

      }
      pw.close();

    }//if
  }//run


  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
              templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    //TODO: create a call to Spark.post to make a post request to a url which
    // will handle getting autocorrect results for the input
    Spark.get("/fridge", new FridgeHandler(), freeMarker);
    Spark.post("/recipe", new RecipeHandler());


  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
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
