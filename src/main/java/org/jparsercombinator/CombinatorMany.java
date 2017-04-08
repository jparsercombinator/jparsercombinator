package org.jparsercombinator;

import java.util.ArrayList;
import java.util.List;

class CombinatorMany<T> implements Combinator<List<T>> {

  private Combinator<T> combinator;
  private Combinator<?> separator;

  CombinatorMany(Combinator<T> combinator) {
    this.combinator = combinator;
  }

  CombinatorMany(Combinator<T> combinator, Combinator<?> separator) {
    this.combinator = combinator;
    this.separator = separator;
  }

  @Override
  public Result<List<T>> apply(String input) {
    List<T> results = new ArrayList<>();

    String lastSuccessfulInput = input;
    Result<T> result = combinator.apply(input);

    while (result.isAccepted()) {
      lastSuccessfulInput = result.remainingInput();

      results.add(result.result());

      if (separator == null) {
        result = combinator.apply(lastSuccessfulInput);
      } else {
        result = applySeparatorThenCombinator(lastSuccessfulInput);
      }
    }

    return new Accept<>(results, lastSuccessfulInput);
  }

  private Result<T> applySeparatorThenCombinator(String input) {
    Result<?> separatorResult = separator.apply(input);

    if (separatorResult.isAccepted()) {
      return combinator.apply(separatorResult.remainingInput());
    } else {
      return new Reject<>(separatorResult.errorMessage());
    }
  }

}
