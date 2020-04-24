package edu.brown.cs.teams.main;

import java.io.*;
import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.json.simple.JSONArray;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

    private static final int DEFAULT_PORT = 4567;
    private static final Gson GSON = new Gson();

    /**
     * The initial method called when execution begins.
     *
     * @param args
     *          An array of command line arguments
     */
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
        parser.accepts("prefix");
        parser.accepts("whitespace");
        OptionSpec<Integer> ledSpec =
                parser.accepts("led").withRequiredArg().ofType(Integer.class);
        OptionSpec<String> dataSpec =
                parser.accepts("data").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);
        if (options.has("gui")) {
            runSparkServer((int) options.valueOf("port"));
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

    private void runSparkServer(int port) {
        Spark.port(port);
        Spark.externalStaticFileLocation("src/main/resources/static");
        Spark.exception(Exception.class, new ExceptionPrinter());
        FreeMarkerEngine freeMarker = createEngine();

        // Setup Spark Routes
        Spark.get("/autocorrect", new AutocorrectHandler(), freeMarker);
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

    /** A handler to produce our autocorrect service site.
     *  @return ModelAndView to render.
     *  (autocorrect.ftl).
     */
    private static class AutocorrectHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("title",
                    "Autocorrect: Generate suggestions", "message", "Build your Autocorrector here!");
            return new ModelAndView(variables, "autocorrect.ftl");
        }
    }

    /** A handler to produce our autocorrect service site.
     *  @return ModelAndView to render.
     *  (autocorrect.ftl).
     */
    private static class FridgeHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("title",
                    "Fridge: Whats in Your Fridge", "message", "");
            return new ModelAndView(variables, "fridge.ftl");
        }
    }


    /** A handler to produce our autocorrect service site.
     *  @return ModelAndView to render.
     *  (autocorrect.ftl).
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

//      Map<String, Object> variables = ImmutableMap.of("name", "Cherry vodka fizz","description", "Start your celebrations in style with a glass of pretty fizz - cherry brandy, kirsch, amaretto and vodka, topped up with sparking grape juice");



            String json = "{\"name\": \"Cherry vodka fizz\", \"author\": \"Good Food\", \"description\": \"Start your celebrations in style with a glass of pretty fizz - cherry brandy, kirsch, amaretto and vodka, topped up with sparking grape juice\", \"nutrition\": {\"kcal\": \"328\", \"fat\": \"0g\", \"saturates\": \"0g\", \"carbs\": \"19g\", \"sugars\": \"19g\", \"fibre\": \"0g\", \"protein\": \"0g\", \"salt\": \"0g\"}, \"ingredients\": [\"50ml cherry brandy liqueur\", \"50ml kirsch\", \"50ml amaretto\", \"100ml vodka\", \"750ml bottle sparkling red grape juice (we used Shloer)\", \"12 maraschino cherries\"], \"method\": [\"Pour the liqueur, kirsch, amaretto and vodka into a jug or cocktail shaker and mix well. Divide between 6 tall glasses and top up with grape juice. Add 2 maraschino cherries to each glass and serve.\"], \"time\": [{\"prep\": {\"hrs\": null, \"mins\": \"5 mins\"}, \"cook\": {\"hrs\": null, \"mins\": null}}], \"difficulty\": [\" Easy \"], \"servings\": \" Serves 6 \", \"img_url\": \"//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe_images/recipe-image-legacy-id--849632_10.jpg?itok=XEBjqMbC\"}";
//
//      JSONParser parser = new JSONParser();
//      JSONObject object = (JSONObject) parser.parse(json);
//      System.out.println(object.toJSONString());
            return json;
        }
    }

}
