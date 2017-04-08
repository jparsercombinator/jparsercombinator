package org.jparsercombinator;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Factory methods for creating common parser combinators.
 */
public final class Combinators {

  private Combinators() {
  }

  public static Combinator<String> string(String s) {
    return regex(Pattern.quote(s));
  }

  public static Combinator<String> regex(String regex) {
    return regexFullResult(regex).map(MatchResult::group);
  }

  public static Combinator<MatchResult> regexFullResult(String regex) {
    return new RegexCombinator(Pattern.compile(regex));
  }

  public static SkipCombinator skip(Combinator<?> skip) {
    return new SkipCombinator(skip);
  }

  public static <T> CombinatorReference<T> newRef() {
    return new CombinatorReference<>();
  }

}
