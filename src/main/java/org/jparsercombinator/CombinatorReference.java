package org.jparsercombinator;

public class CombinatorReference<T> implements Combinator<T> {

  private Combinator<T> combinator;

  CombinatorReference() {
  }

  public void setCombinator(Combinator<T> combinator) {
    this.combinator = combinator;
  }

  @Override
  public Result<T> apply(String input) {
    return combinator.apply(input);
  }

}
