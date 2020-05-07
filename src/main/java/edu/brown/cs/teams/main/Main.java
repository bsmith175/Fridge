package edu.brown.cs.teams.main;


import edu.brown.cs.teams.GUI.GuiHandlers;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;


/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {
  private static final int DEFAULT_PORT = 4567;
  private static REPL repl = null;

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
    parser.accepts("init");
    OptionSet options = parser.parse(args);

    RecipeDatabase r;
    UserDatabase u;

    if (options.has("init")) {
      try {


        r = new RecipeDatabase(Constants.DATABASE_FILE, true);
        r.makeTable();
        r.parseJson();
        URI dbURI = new URI(System.getenv("DATABASE_URL"));
        String username = dbURI.getUserInfo().split(":")[0];
        String pwd = dbURI.getUserInfo().split(":")[1];

        String dbURL = "jdbc:postgresql://" + dbURI.getHost() + ':' + dbURI
            .getPort() + dbURI.getPath();
        u = new UserDatabase(dbURL, username, pwd, true);
        u.initUserDB();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (CommandException e) {
        e.printStackTrace();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }

    try {
      r = new RecipeDatabase(Constants.DATABASE_FILE, false);
//      URI dbURI = new URI(System.getenv("DATABASE_URL"));
      //for debuggin:
      URI dbURI = new URI("postgres://dzocvwcilygobn:a9403fa911846decf8edddd920cb6e3c1b6bed669f690dab6b69d0c818b98983@ec2-18-215-99-63.compute-1.amazonaws.com:5432/dbhl22glfabs41");

      String username = dbURI.getUserInfo().split(":")[0];
      String pwd = dbURI.getUserInfo().split(":")[1];

      String dbURL = "jdbc:postgresql://" + dbURI.getHost() + ':' + dbURI
          .getPort() + dbURI.getPath();

      u = new UserDatabase(dbURL, username, pwd, false);
      System.out.println("Getting recipes");
      AlgUtils.setDb(r, u);
      AlgUtils.buildRecList();
      List<MinimalRecipe> recipes = AlgUtils.getMinimalRecipesList();
      AlgUtils.setTree(AlgUtils.getTree().buildKDTree(recipes));
      System.out.println("ready to query");
      runSparkServer((int) options.valueOf("port"));

    } catch (CommandException e) {
      System.out.println(e.getMessage());
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }


    if (options.has("repl")) {
      // Allow the repl to run both commands
      HashMap<String, Command> commands = new HashMap<>();
      commands.put("recommend", new RecipeSuggest(false, false, false));
      repl = new REPL(commands);
      repl.runREPL();
    }

    if (options.has("gui")) {
      try {
        runSparkServer((int) options.valueOf("port"));
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  } //run


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
    Spark.port(getHerokuAssignedPort());
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    // Setup Spark Routes
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

  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
  }
}
