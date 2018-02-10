package org.jparsercombinator;

/**
 * Class to bootstrap the skipping of first parser. An instance of {@code SkipCombinator} is created
 * by {@code ParserCombinators.skip(...)}.
 */
public class SkipCombinator {

  private ParserCombinator<?> skip;

  SkipCombinator(ParserCombinator<?> skip) {
    this.skip = skip;
  }

  /**
   * Create a {@code ParserCombinator} that skips and applies next. Skip combinator is created using
   * {@code ParserCombinators.skip(...)}. For example {@code skip(string("foo")).next(string("bar"))}
   * would parse "foobar" returning "bar".
   *
   * @param next parser to be applied
   * @param <T> type of parse result
   * @return parser that first skips then applies next
   */
  public <T> ParserCombinator<T> next(ParserCombinator<T> next) {
    return input -> {
      Result<?> skipResult = skip.apply(input);

      if (skipResult.isAccepted()) {
        return next.apply(skipResult.remainingInput());
      } else {
        return new Reject<>(skipResult.errorMessage());
      }
    };
  }

}
