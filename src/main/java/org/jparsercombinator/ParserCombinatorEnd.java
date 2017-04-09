package org.jparsercombinator;

class ParserCombinatorEnd<T> implements Parser<T> {

  private ParserCombinator<T> combinator;

  ParserCombinatorEnd(ParserCombinator<T> combinator) {
    this.combinator = combinator;
  }

  @Override
  public T apply(String input) {
    Result<T> result = combinator.apply(input);

    if (result.isAccepted()) {
      String remainingInput = result.remainingInput();

      if (remainingInput.isEmpty()) {
        return result.result();
      } else {
        throw new ParseException("Parse incomplete: " + remainingInput);
      }
    }

    throw new ParseException(result.errorMessage());
  }

}
