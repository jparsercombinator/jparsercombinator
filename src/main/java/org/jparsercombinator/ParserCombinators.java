package org.jparsercombinator;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Factory methods for creating common parser combinators.
 */
public final class ParserCombinators {

  private ParserCombinators() {
  }

  /**
   * Creates a {@code ParserCombinator} that accepts given string only.
   *
   * @param str string accepted by the parser
   * @return the string that was accepted
   */
  public static ParserCombinator<String> string(String str) {
    return regex(Pattern.quote(str));
  }

  /**
   * Creates a {@code ParserCombinator} that accepts inputs matching given regex.
   *
   * @param regex pattern accepted by the parser
   * @return the matching input as a whole
   */
  public static ParserCombinator<String> regex(String regex) {
    return regexMatchResult(regex).map(MatchResult::group);
  }

  /**
   * Creates a {@code ParserCombinator} that accepts inputs matching given regex.
   *
   * @param regex pattern accepted by the parser
   * @return the regex match result for further grouping etc.
   */
  public static ParserCombinator<MatchResult> regexMatchResult(String regex) {
    return new RegexParserCombinator(Pattern.compile(regex));
  }

  /**
   * Creates a {@code SkipCombinator} for building a {@code ParserCombinator} that skips first then
   * applies next combinator. For example {@code skip(string("(")).next(string("foo")).skip(string(")"))}
   * parses parenthesized string "foo".
   *
   * @param skip a parser to be skipped
   * @return {@code SkipCombinator} that provides {@code next(ParserCombinator<T> next)} for
   * building a new {@code ParserCombinator} that skips the first combinator then applies next.
   */
  public static SkipCombinator skip(ParserCombinator<?> skip) {
    return new SkipCombinator(skip);
  }

  /**
   * Creates a {@code ParserCombinatorReference} that can be used to create self referencing
   * recursive parsers. {@code ParserCombinatorReference} is simply a minimal implementation of the
   * delegate pattern.
   *
   * @param <T> type of the parse result
   * @return a new {@code ParserCombinatorReference}
   */
  public static <T> ParserCombinatorReference<T> newRef() {
    return new ParserCombinatorReference<>();
  }

}
