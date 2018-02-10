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

  /**
   * Builds a new parser combinator for parsing sequences. After successful parse, parser returns
   * pair of results where the first element is parsed by {@code this} and the second element by
   * {@code next} parser. For example {@code string("foo").next(string("bar"))} would accept {@code
   * "foobar"} and produce {@code ("foo", "bar")}.
   *
   * @param next next parser in the sequence
   * @param <R> type of the value accepted by next parser
   * @return new parser combinator that accepts inputs applying first {@code this} then {@code next}
   * parser
   */
  default <R> ParserCombinator<Pair<T, R>> next(ParserCombinator<R> next) {
    return new ParserCombinatorNext<>(this, next);
  }

  /**
   * Builds a new parser combinator that first applies {@code this} parser then {@code skip} parser
   * returning results only from the first parser. For example {@code
   * string("foo").skip(string("bar"))} would consume {@code "foobar"} and return {@code "foo"}.
   *
   * @param skip parser that ignores parse results
   * @return new parser combinator that accepts inputs applying first {@code this} then {@code skip}
   * parser returning results only from {@code this}.
   */
  default ParserCombinator<T> skip(ParserCombinator<?> skip) {
    return new ParserCombinatorSkip<>(this, skip);
  }

  /**
   * Builds a new parser combinator applies {@code this} parser repeatedly zero to N times returning
   * results in list. For example {@code string("foo").many()} would parse {@code "foofoo"} by
   * returning {@code ["foo", "foo"]}
   *
   * @return new parser combinator that accepts inputs applying {@code this} parser as many times as
   * possible.
   */
  default ParserCombinator<List<T>> many() {
    return new ParserCombinatorMany<>(this);
  }

  /**
   * Builds a new parser combinator applies {@code this} parser repeatedly zero to N times by using
   * given parser as a separator. Results are returned in a list. For example {@code
   * string("foo").many(string(";"))} would parse {@code "foo;foo;foo"} by returning {@code ["foo",
   * "foo", "foo"]}
   *
   * @param separator parser for parsing separator
   * @return new parser combinator that accepts inputs applying {@code this} parser as many times as
   * possible.
   */
  default ParserCombinator<List<T>> many(ParserCombinator<?> separator) {
    return new ParserCombinatorManyWithSeparator<>(this, separator);
  }

  /**
   * Builds a new parser combinator applies {@code this} parser repeatedly zero or one time. For
   * example {@code string("foo").optional().next(string("bar"))} would parse {@code "foobar"} by
   * returning {@code (Optional["foo"], "bar")}, and {@code "bar"} by returning {@code
   * (Optional.empty, "bar")}.
   *
   * @return new parser combinator that accepts inputs applying {@code this} parser as many times as
   * possible.
   */
  default ParserCombinator<Optional<T>> optional() {
    return new ParserCombinatorOptional<>(this);
  }

  /**
   * Builds a new parser combinator that maps parse results with given function. Can be used for
   * transforming parsed values to something more useful, e.g. {@code regex("[0-9]+").map(value ->
   * Integer.parseInt(value));}
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
