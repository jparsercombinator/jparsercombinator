package org.jparsercombinator;

public interface Parser<T> {

  /**
   * @param input to be parsed
   * @return parse result, e.g. an AST
   */
  T apply(String input);

}
