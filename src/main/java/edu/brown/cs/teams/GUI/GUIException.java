package edu.brown.cs.teams.GUI;

/**
 * Exception to be triggered when the command is malformed.
 */
public class GUIException extends Exception {
  /**
   * Constructor for a Command Exception.
   * @param message String error message
   */
  public GUIException(String message) {
    super(message);
  }
}
