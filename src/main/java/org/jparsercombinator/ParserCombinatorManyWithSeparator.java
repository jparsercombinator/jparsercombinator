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

    for (Result<T> r = combinator.apply(input); r.isAccepted(); r = applyWithSeparator(input)) {
      results.add(r.result());
      input = r.remainingInput();
    }

    return new Accept<>(results, input);
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
