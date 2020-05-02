package edu.brown.cs.teams.main;


import edu.brown.cs.teams.GUI.GuiHandlers;
import edu.brown.cs.teams.algorithms.RunKDAlg;
import edu.brown.cs.teams.algorithms.RecipeSuggest;
import edu.brown.cs.teams.algorithms.AlgUtils;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.database.UserDatabase;
import edu.brown.cs.teams.io.Command;
import edu.brown.cs.teams.io.CommandException;
import edu.brown.cs.teams.io.REPL;
import edu.brown.cs.teams.recipe.MinimalRecipe;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONException;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import spark.*;
import spark.template.freemarker.FreeMarkerEngine;


/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {
  private static final int DEFAULT_PORT = 4567;
  private static REPL REPL = null;

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
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);
    parser.accepts("database");
    parser.accepts("ben");
    parser.accepts("repl");
    parser.accepts("sqlite_init");
    parser.accepts("postgres_init");
    OptionSet options = parser.parse(args);
    RecipeDatabase r = null;
    UserDatabase u = null;

    if (options.has("init")) {
      try {
        r = new RecipeDatabase(Constants.DATABASE_FILE, true);
        r.makeTable();
        r.parseJson();

        String dbURL = "jdbc:postgresql://" + Constants.DB_HOST +
                ":" + Constants.DB_PORT + "/" + Constants.DB_NAME;
        u = new UserDatabase(dbURL, Constants.DB_USERNAME, Constants.DB_PWD,
                true);
        u.initUserDB();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (CommandException e) {
        e.printStackTrace();
      }
    }

    try {
      r = new RecipeDatabase(Constants.DATABASE_FILE, false);
      String dbURL = "jdbc:postgresql://" + Constants.DB_HOST +
              ":" + Constants.DB_PORT + "/" + Constants.DB_NAME;
      u = new UserDatabase(dbURL, Constants.DB_USERNAME, Constants.DB_PWD,
              false);
      System.out.println("Getting recipes");
      AlgUtils.setDb(r, u);
      List<MinimalRecipe> recipes = AlgUtils.getMinimalRecipesList();
      AlgUtils.setTree(AlgUtils.getTree().buildKDTree(recipes));
      AlgUtils.buildRecList();
      System.out.println("ready to query");
    } catch (CommandException e) {
      System.out.println(e.getMessage());
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }


    if (options.has("repl")) {
      // Allow the repl to run both commands
      HashMap<String, Command> commands = new HashMap<>();
      commands.put("recommend", new RecipeSuggest(false, false, false));
      REPL = new REPL(commands);
      REPL.runREPL();
    }

    if (options.has("gui")) {
      try {
        runSparkServer((int) options.valueOf("port"));
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
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


  private void runSparkServer(int port) throws Exception {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    //TODO: create a call to Spark.post to make a post request to a url which
    // will handle getting autocorrect results for the input
    GuiHandlers handler = new GuiHandlers();
    handler.setHandlers(freeMarker);

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


}
