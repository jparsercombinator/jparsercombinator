package org.jparsercombinator;

class CombinatorNext<T, R> implements Combinator<Pair<T, R>> {

  private Combinator<T> combinator;
  private Combinator<R> next;

  CombinatorNext(Combinator<T> combinator, Combinator<R> next) {
    this.combinator = combinator;
    this.next = next;
  }

  @Override
  public Result<Pair<T, R>> apply(String input) {
    Result<T> result = combinator.apply(input);

    if (result.isAccepted()) {
      Result<R> nextResult = next.apply(result.remainingInput());

      if (nextResult.isAccepted()) {
        Pair<T, R> tuple = new Pair<>(result.result(), nextResult.result());
        return new Accept<>(tuple, nextResult.remainingInput());
      } else {
        return new Reject<>(nextResult.errorMessage());
      }
    }

    return new Reject<>(result.errorMessage());
  }

}
