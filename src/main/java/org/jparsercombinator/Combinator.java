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
public interface Combinator<T> extends Parser<Result<T>> {

  default Combinator<T> or(Combinator<T> or) {
    return new CombinatorOr<>(this, or);
  }

  default <R> Combinator<Pair<T, R>> next(Combinator<R> next) {
    return new CombinatorNext<>(this, next);
  }

  default Combinator<T> skip(Combinator<?> skip) {
    return new CombinatorSkip<>(this, skip);
  }

  default Combinator<List<T>> many() {
    return new CombinatorMany<>(this);
  }

  default Combinator<List<T>> many(Combinator<?> separator) {
    return new CombinatorMany<>(this, separator);
  }

  default Combinator<Optional<T>> optional() {
    return new CombinatorOptional<>(this);
  }

  default <R> Combinator<R> map(Function<T, R> mapping) {
    return new CombinatorMap<>(this, mapping);
  }

}
