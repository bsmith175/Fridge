package edu.brown.cs.teams.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * REPL class to accept user input from and output to the command line.
 */
public class REPL {
  private static BufferedReader userReader =
          new BufferedReader(new InputStreamReader(System.in));
  private HashMap<String, Command> commands;

  /**
   * Constructor for the REPL class.
   *
   * @param commands map from command name to command object
   */
  public REPL(HashMap<String, Command> commands) {
    this.commands = commands;
  }

  /**
   * Main method that runs the entire program. This will constantly loop until the
   * user kills the program, accept input from the command line, call the appropriate
   * command, and return the output of the input.
   */
  public void runREPL() {
    String[] input;
    while (true) {
      try {
        input = REPL.getNextInput();
        if (input.length > 0) {
          Command command = this.commands.get(input[0]);
          if (command != null) {
            String output = command.runCommand(input);
            if (!output.equals("")) {
              System.out.println(output);
            }
          } else {
            throw new CommandException("ERROR: Command not found: " + input[0]);
          }
        }
      } catch (CommandException | SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /*
  Gets the next input from the command line and returns it as a string
  array that is split using a regex
   */
  private static String[] getNextInput() {
    try {
      String line = userReader.readLine();
      if (line == null) {
        System.exit(0);
      }
      // remove all trailing and leading whitespace and split on all
      // whitespace using a regex
      // regex code obtained from:
      // https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
      // a TA on piazza said it was fine to use the code as long as I cited it.
      List<String> matchList = new ArrayList<>();
      Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
      Matcher regexMatcher = regex.matcher(line);
      while (regexMatcher.find()) {
        matchList.add(regexMatcher.group());
      }
      return matchList.toArray(new String[matchList.size()]);
    } catch (IOException error) {
      System.exit(1);
      return new String[0];
    }
  }

  /**
   * Removes quotes from a string and throws an exception if quotes are not found.
   *
   * @param s string to remove quotes from
   * @return s without wrapping quotes
   * @throws CommandException if the string doesn't have quotes.
   */
  public static String removeQuotes(String s) throws CommandException {
    int length = s.length();
    if (length >= 2 && s.charAt(0) == '\"' && s.charAt(length - 1) == '\"') {
      return s.substring(1, length - 1);
    } else {
      throw new CommandException("ERROR: argument is not wrapped in"
              + " quotes correctly: " + s);
    }
  }
}
