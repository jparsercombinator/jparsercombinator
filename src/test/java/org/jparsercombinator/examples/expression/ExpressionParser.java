package org.jparsercombinator.examples.expression;

import static org.jparsercombinator.Combinators.regex;
import static org.jparsercombinator.Combinators.skip;
import static org.jparsercombinator.Combinators.string;

import java.util.List;
import org.jparsercombinator.Combinator;
import org.jparsercombinator.CombinatorReference;
import org.jparsercombinator.Combinators;
import org.jparsercombinator.Pair;
import org.jparsercombinator.Parser;
import org.jparsercombinator.Parsers;

/**
 * Test parsing and evaluating of expressions of form:
 *
 * <expression> ::= [ "-" ] <term> [ ( "+" | "-" ) <term> ]*
 * <term>       ::= <factor> [ ( "*" | "/" ) <factor> ]*
 * <factor>     ::= <number> | ( "(" <expression> ")" )
 */
class ExpressionParser implements Parser<Integer> {

  private static Parser<Integer> parser;

  ExpressionParser() {
    Combinator<Integer> parseInteger = regex("[0-9]+").map(Integer::parseInt);

    // need to be defined as reference to avoid illegal self reference
    CombinatorReference<Integer> expression = Combinators.newRef();
    CombinatorReference<Integer> term = Combinators.newRef();
    CombinatorReference<Integer> factor = Combinators.newRef();

    Combinator<Integer> firstTerm = string("-").optional().next(term)
        .map(p -> p.first.isPresent() ? -p.second : p.second);
    Combinator<List<Pair<String, Integer>>> restOfTheTerms =
        string("+").or(string("-")).next(term).many();

    expression.setCombinator(firstTerm.next(restOfTheTerms)
        .map(input -> {
          int result = input.first;
          for (Pair<String, Integer> p : input.second) {
            switch (p.first) {
              case "+": result += p.second; break;
              case "-": result -= p.second; break;
              default: throw new IllegalStateException();
            }
          }
          return result;
        }));

    Combinator<List<Pair<String, Integer>>> restOfTheFactors =
        string("*").or(string("/")).next(factor).many();

    term.setCombinator(factor.next(restOfTheFactors)
        .map(input -> {
          int result = input.first;
          for (Pair<String, Integer> p : input.second) {
            switch (p.first) {
              case "*": result *= p.second; break;
              case "/": result /= p.second; break;
              default: throw new IllegalStateException();
            }
          }
          return result;
        }));

    Combinator<Integer> parenthesizedExpression =
        skip(string("(")).next(expression).skip(string(")"));

    factor.setCombinator(parseInteger.or(parenthesizedExpression));

    parser = Parsers.parser(expression);
  }

  @Override
  public Integer apply(String input) {
    return parser.apply(input);
  }

}
