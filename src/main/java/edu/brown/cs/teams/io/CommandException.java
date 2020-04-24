package edu.brown.cs.teams.io;

/**
 * Exception to be triggered when the command is malformed.
 */
public class CommandException extends Exception {
  /**
   * Constructor for a Command Exception.
   * @param message String error message
   */
  public CommandException(String message) {
    super(message);
  }
}
