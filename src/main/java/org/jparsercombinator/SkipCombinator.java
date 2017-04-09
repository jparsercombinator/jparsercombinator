package org.jparsercombinator;

public class SkipCombinator {

  private ParserCombinator<?> skip;

  SkipCombinator(ParserCombinator<?> skip) {
    this.skip = skip;
  }

  public <T> ParserCombinator<T> next(ParserCombinator<T> next) {
    return input -> {
      Result<?> skipResult = skip.apply(input);

      if (skipResult.isAccepted()) {
        return next.apply(skipResult.remainingInput());
      }

      return new Reject<>(skipResult.errorMessage());
    };
  }

}
