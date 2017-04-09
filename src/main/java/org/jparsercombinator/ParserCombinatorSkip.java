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
