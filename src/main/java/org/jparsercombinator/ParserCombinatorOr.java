package org.jparsercombinator;

class ParserCombinatorOr<T> implements ParserCombinator<T> {

  private ParserCombinator<T> combinator;
  private ParserCombinator<T> or;

  ParserCombinatorOr(ParserCombinator<T> combinator, ParserCombinator<T> or) {
    this.combinator = combinator;
    this.or = or;
  }

  @Override
  public Result<T> apply(String input) {
    Result<T> result = combinator.apply(input);
    return result.isAccepted() ? result : or.apply(input);
  }

}
