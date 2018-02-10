package org.jparsercombinator;

class ParserCombinatorSkip<T> implements ParserCombinator<T> {

  private ParserCombinator<T> combinator;
  private ParserCombinator<?> skip;

  ParserCombinatorSkip(ParserCombinator<T> combinator, ParserCombinator<?> skip) {
    this.combinator = combinator;
    this.skip = skip;
  }

  @Override
  public Result<T> apply(String input) {
    Result<T> result = combinator.apply(input);

    if (result.isAccepted()) {
      return resultSkipNext(result.result(), result.remainingInput());
    } else {
      return new Reject<>(result.errorMessage());
    }
  }

  private Result<T> resultSkipNext(T result, String input) {
    Result<?> skippedResult = skip.apply(input);

    if (skippedResult.isAccepted()) {
      return new Accept<>(result, skippedResult.remainingInput());
    } else {
      return new Reject<>(skippedResult.errorMessage());
    }
  }

}
