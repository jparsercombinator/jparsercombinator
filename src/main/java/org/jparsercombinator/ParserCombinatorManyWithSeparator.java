package org.jparsercombinator;

import java.util.ArrayList;
import java.util.List;

class ParserCombinatorManyWithSeparator<T> implements ParserCombinator<List<T>> {

  private ParserCombinator<T> combinator;
  private ParserCombinator<?> separator;

  ParserCombinatorManyWithSeparator(ParserCombinator<T> combinator, ParserCombinator<?> separator) {
    this.combinator = combinator;
    this.separator = separator;
  }

  @Override
  public Result<List<T>> apply(String input) {
    List<T> results = new ArrayList<>();

    String remainingInput = input;
    Result<T> lastResult = combinator.apply(remainingInput);

    while (lastResult.isAccepted()) {
      results.add(lastResult.result());
      remainingInput = lastResult.remainingInput();
      lastResult = applyWithSeparator(remainingInput);
    }

    return new Accept<>(results, remainingInput);
  }

  private Result<T> applyWithSeparator(String input) {
    Result<?> separatorResult = separator.apply(input);

    if (separatorResult.isAccepted()) {
      return combinator.apply(separatorResult.remainingInput());
    } else {
      return new Reject<>(separatorResult.errorMessage());
    }
  }

}
