package org.jparsercombinator;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Parser combinator is a parser that is built by composing several other combinators to create
 * more complex parsers. Parser combinator returns a result object that indicates whether input was
 * accepted or rejected. If input was accepted, remaining input is also returned for further
 * parsing.
 */
public interface ParserCombinator<T> extends Parser<Result<T>> {

  /**
   * Builds a new alternating parser from {@code this} and given parser. The new parser parses with
   * either {@code this} or the given parser, which ever first succeeds. If neither succeeds, input
   * is rejected. Multiple {@code or} calls can be chained for more alternatives (e.g. {@code
   * string("A").or(string("B")).or(string("C"))}.
   *
   * @param or alternative parser
   * @return new parser combinator that accepts inputs either by {@code this} or given parser.
   */
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

  /**
   * Creates a new {@code ParserCombinator} that maps parse results with given function. Can be used
   * for transforming parsed values to something more useful, e.g. {@code regex("[0-9]+").map(value
   * -> Integer.parseInt(value));}
   *
   * @param mapping function that maps parse result
   * @param <R> type of the target value
   * @return new parser that maps result
   */
  default <R> ParserCombinator<R> map(Function<T, R> mapping) {
    return new ParserCombinatorMap<>(this, mapping);
  }

  /**
   * Closes this {@code ParserCombinator} and returns a new plain {@code Parser}. New parser expects
   * input to be fully consumed. Returned parser throws a {@code ParseException} on failed parse.
   *
   * @return Parser based on this {@code ParserCombinator}
   */
  default Parser<T> end() {
    return new ParserCombinatorEnd<>(this);
  }

}
