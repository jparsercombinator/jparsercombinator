package org.jparsercombinator;

class ParserCombinatorNext<T, R> implements ParserCombinator<Pair<T, R>> {

  private ParserCombinator<T> combinator;
  private ParserCombinator<R> next;

  ParserCombinatorNext(ParserCombinator<T> combinator, ParserCombinator<R> next) {
    this.combinator = combinator;
    this.next = next;
  }

  @Override
  public Result<Pair<T, R>> apply(String input) {
    Result<T> first = combinator.apply(input);

    if (first.isAccepted()) {
      return applyNext(first.result(), first.remainingInput());
    } else {
      return new Reject<>(first.errorMessage());
    }
  }

  private Result<Pair<T, R>> applyNext(T first, String input) {
    Result<R> nextResult = next.apply(input);

    if (nextResult.isAccepted()) {
      return new Accept<>(new Pair<>(first, nextResult.result()), nextResult.remainingInput());
    } else {
      return new Reject<>(nextResult.errorMessage());
    }
  }

}
