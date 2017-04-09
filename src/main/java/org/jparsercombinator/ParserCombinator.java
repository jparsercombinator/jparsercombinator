package org.jparsercombinator;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Parser combinator is a parser that is built by composing several other combinators to create
 * more complex parsers. This combinator returns a result object that indicates whether input was
 * accepted or rejected. If input was accepted, remaining input is also returned for further
 * parsing.
 */
public interface ParserCombinator<T> extends Parser<Result<T>> {

  default ParserCombinator<T> or(ParserCombinator<T> or) {
    return new ParserCombinatorOr<>(this, or);
  }

  default <R> ParserCombinator<Pair<T, R>> next(ParserCombinator<R> next) {
    return new ParserCombinatorNext<>(this, next);
  }

  default ParserCombinator<T> skip(ParserCombinator<?> skip) {
    return new ParserCombinatorSkip<>(this, skip);
  }

  default ParserCombinator<List<T>> many() {
    return new ParserCombinatorMany<>(this);
  }

  default ParserCombinator<List<T>> many(ParserCombinator<?> separator) {
    return new ParserCombinatorMany<>(this, separator);
  }

  default ParserCombinator<Optional<T>> optional() {
    return new ParserCombinatorOptional<>(this);
  }

  default <R> ParserCombinator<R> map(Function<T, R> mapping) {
    return new ParserCombinatorMap<>(this, mapping);
  }

  default Parser<T> end() {
    return new ParserCombinatorEnd<>(this);
  }

}
