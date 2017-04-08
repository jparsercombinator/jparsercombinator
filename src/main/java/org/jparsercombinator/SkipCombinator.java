package org.jparsercombinator;

public class SkipCombinator {

  private Combinator<?> skip;

  SkipCombinator(Combinator<?> skip) {
    this.skip = skip;
  }

  public <T> Combinator<T> next(Combinator<T> next) {
    return input -> {
      Result<?> skipResult = skip.apply(input);

      if (skipResult.isAccepted()) {
        return next.apply(skipResult.remainingInput());
      }

      return new Reject<>(skipResult.errorMessage());
    };
  }

}
