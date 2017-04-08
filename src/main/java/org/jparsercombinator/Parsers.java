package org.jparsercombinator;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Convenience functions to create simple error handling parsers from parser combinators.
 */
public final class Parsers {

  private Parsers() {
  }

  /**
   * Wraps parser combinator to a parser that throws ParseException on failed parse.
   */
  public static <T> Parser<T> parser(Combinator<T> combinator) {
    return input -> {
      Result<T> result = combinator.apply(input);

      if (result.isAccepted()) {
        if (result.remainingInput().isEmpty()) {
          return result.result();
        } else {
          throw new ParseException("Parse incomplete: " + result.remainingInput());
        }
      }

      throw new ParseException(result.errorMessage());
    };
  }

  /**
   * Wraps parser combinator to a Optional returning Parser. Empty optional denotes failed parse.
   * Error messages are supplied to given consumer.
   */
  public static <T> Parser<Optional<T>> parser(Combinator<T> combinator,
      Consumer<String> errorMessageConsumer) {
    return input -> {
      Result<T> result = combinator.apply(input);

      if (result.isAccepted()) {
        if (result.remainingInput().isEmpty()) {
          return Optional.of(result.result());
        } else {
          errorMessageConsumer.accept("Parse incomplete: " + result.remainingInput());
          return Optional.empty();
        }
      }

      errorMessageConsumer.accept(result.errorMessage());
      return Optional.empty();
    };
  }

}
