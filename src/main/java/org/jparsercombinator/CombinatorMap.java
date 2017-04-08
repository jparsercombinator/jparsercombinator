package org.jparsercombinator;

import java.util.function.Function;

class CombinatorMap<T, R> implements Combinator<R> {

  private Combinator<T> combinator;
  private Function<T, R> mapping;

  CombinatorMap(Combinator<T> combinator, Function<T, R> mapping) {
    this.combinator = combinator;
    this.mapping = mapping;
  }

  @Override
  public Result<R> apply(String input) {
    Result<T> result = combinator.apply(input);

    if (result.isAccepted()) {
      return new Accept<>(mapping.apply(result.result()), result.remainingInput());
    }

    return new Reject<>(result.errorMessage());
  }

}
