package org.jparsercombinator;

import java.util.Optional;

class CombinatorOptional<T> implements Combinator<Optional<T>> {

  private Combinator<T> combinator;

  CombinatorOptional(Combinator<T> combinator) {
    this.combinator = combinator;
  }

  @Override
  public Result<Optional<T>> apply(String input) {
    Result<T> result = combinator.apply(input);

    if (result.isAccepted()) {
      return new Accept<>(Optional.of(result.result()), result.remainingInput());
    }

    return new Accept<>(Optional.empty(), input);
  }

}
