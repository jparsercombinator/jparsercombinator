package org.jparsercombinator;

/**
 * {@code Parser} reads {@code String} input and returns result of type {@code T}.
 *
 * @param <T> type of the parse result
 */
public interface Parser<T> {

  /**
   * Parses input and returns a result of type {@code T}. May throw {@code ParseException} depending
   * on the implementation.
   *
   * @param input to be parsed
   * @return parse result, e.g. an AST
   */
  T apply(String input);

}
