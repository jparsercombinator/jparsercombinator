package org.jparsercombinator;

/**
 * Signals a failed parse.
 */
public class ParseException extends RuntimeException {

  ParseException(String message) {
    super(message);
  }

}
