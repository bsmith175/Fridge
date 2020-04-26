package edu.brown.cs.teams.io;


import com.google.gson.JsonObject;

import java.util.List;

/**
 * Interface that all commands must implement. Guarantees that every command can be
 * executed via the runCommand method.
 */
public interface Command {
  /**
   * Executes the command based on the user's input.
   * @param command user input string split on whitespace
   * @throws CommandException if there is an error in the command format
   * @return output of the command to be printed to the repl
   */
  String runCommand(String[] command) throws CommandException;

  List<JsonObject> runForGui(String[] command) throws CommandException;
}
