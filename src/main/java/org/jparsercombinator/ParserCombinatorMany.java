package org.jparsercombinator;

import java.util.ArrayList;
import java.util.List;

class ParserCombinatorMany<T> implements ParserCombinator<List<T>> {

  private ParserCombinator<T> combinator;

  ParserCombinatorMany(ParserCombinator<T> combinator) {
    this.combinator = combinator;
  }

  @Override
  public Result<List<T>> apply(String input) {
    List<T> results = new ArrayList<>();

    String remainingInput = input;
    Result<T> lastResult = combinator.apply(remainingInput);

    while (lastResult.isAccepted()) {
      results.add(lastResult.result());
      remainingInput = lastResult.remainingInput();
      lastResult = combinator.apply(remainingInput);
    }

    return new Accept<>(results, remainingInput);
  }

}
