package org.jparsercombinator;

class CombinatorOr<T> implements Combinator<T> {

  private Combinator<T> combinator;
  private Combinator<T> or;

  CombinatorOr(Combinator<T> combinator, Combinator<T> or) {
    this.combinator = combinator;
    this.or = or;
  }

  @Override
  public Result<T> apply(String input) {
    Result<T> result = combinator.apply(input);
    return result.isAccepted() ? result : or.apply(input);
  }

}
