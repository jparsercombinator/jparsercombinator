package org.jparsercombinator;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexParserCombinator implements ParserCombinator<MatchResult> {

  private Pattern pattern;

  RegexParserCombinator(Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public Result<MatchResult> apply(String input) {
    Matcher matcher = pattern.matcher(input);

    if (matcher.lookingAt()) {
      return new Accept<>(matcher.toMatchResult(), input.substring(matcher.end()));
    } else {
      return new Reject<>(input + " does not match " + matcher.pattern());
    }
  }

}
