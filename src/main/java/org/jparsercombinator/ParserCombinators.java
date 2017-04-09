package org.jparsercombinator;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Factory methods for creating common parser combinators.
 */
public final class ParserCombinators {

  private ParserCombinators() {
  }

  public static ParserCombinator<String> string(String s) {
    return regex(Pattern.quote(s));
  }

  public static ParserCombinator<String> regex(String regex) {
    return regexFullResult(regex).map(MatchResult::group);
  }

  public static ParserCombinator<MatchResult> regexFullResult(String regex) {
    return new RegexParserCombinator(Pattern.compile(regex));
  }

  public static SkipCombinator skip(ParserCombinator<?> skip) {
    return new SkipCombinator(skip);
  }

  public static <T> ParserCombinatorReference<T> newRef() {
    return new ParserCombinatorReference<>();
  }

}
