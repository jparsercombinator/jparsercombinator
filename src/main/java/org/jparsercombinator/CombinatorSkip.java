package org.jparsercombinator;

class CombinatorSkip<T> implements Combinator<T> {

  private Combinator<T> combinator;
  private Combinator<?> skip;

  CombinatorSkip(Combinator<T> combinator, Combinator<?> skip) {
    this.combinator = combinator;
    this.skip = skip;
  }

  @Override
  public Result<T> apply(String input) {
    Result<T> result = combinator.apply(input);

    if (result.isAccepted()) {
      Result<?> skippedResult = skip.apply(result.remainingInput());

      if (skippedResult.isAccepted()) {
        return new Accept<>(result.result(), skippedResult.remainingInput());
      } else {
        return new Reject<>(skippedResult.errorMessage());
      }
    }

    return new Reject<>(result.errorMessage());
  }

}
