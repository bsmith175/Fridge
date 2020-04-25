package edu.brown.cs.teams.main;


import edu.brown.cs.teams.GUI.GuiHandlers;
import edu.brown.cs.teams.constants.Constants;
import edu.brown.cs.teams.database.RecipeDatabase;
import edu.brown.cs.teams.ingredientParse.IngredientSuggest;
import edu.brown.cs.teams.io.CommandException;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONException;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

import spark.*;
import spark.template.freemarker.FreeMarkerEngine;


/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {
  private static final int DEFAULT_PORT = 4567;

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

    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      try {
        runSparkServer((int) options.valueOf("port"));
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    if (options.has("sqlite_init")) {
      RecipeDatabase r = null;
      try {
        r = new RecipeDatabase(Constants.DATABASE_FILE, true);
        r.makeTable();
        r.parseJson();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }  catch (JSONException e) {
        e.printStackTrace();
      } catch (CommandException e) {
        e.printStackTrace();
      }
    } else if (options.has("postgres_init")) {
      try {
        String dbURL = "jdbc:postgresql://" + Constants.DB_HOST +
                ":" + Constants.DB_PORT + "/" + Constants.DB_NAME;
        RecipeDatabase r = new RecipeDatabase(dbURL, Constants.DB_USERNAME, Constants.DB_PWD, true);
        r.makeTable();
        r.parseJson();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }  catch (JSONException e) {
        e.printStackTrace();
      } catch (CommandException e) {
        e.printStackTrace();
      }
    }
  }


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
