package org.jparsercombinator;

import java.util.Optional;

class ParserCombinatorOptional<T> implements ParserCombinator<Optional<T>> {

  private ParserCombinator<T> combinator;

  ParserCombinatorOptional(ParserCombinator<T> combinator) {
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
