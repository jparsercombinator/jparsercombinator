package org.jparsercombinator.examples.expression;

import static org.jparsercombinator.ParserCombinators.regex;
import static org.jparsercombinator.ParserCombinators.skip;
import static org.jparsercombinator.ParserCombinators.string;

import java.util.List;
import org.jparsercombinator.ParserCombinator;
import org.jparsercombinator.ParserCombinatorReference;
import org.jparsercombinator.ParserCombinators;
import org.jparsercombinator.Pair;
import org.jparsercombinator.Parser;

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
    ParserCombinator<Integer> parseInteger = regex("[0-9]+").map(Integer::parseInt);

    // need to be defined as reference to avoid illegal self reference
    ParserCombinatorReference<Integer> expression = ParserCombinators.newRef();
    ParserCombinatorReference<Integer> term = ParserCombinators.newRef();
    ParserCombinatorReference<Integer> factor = ParserCombinators.newRef();

    ParserCombinator<Integer> firstTerm = string("-").optional().next(term)
        .map(p -> p.first.isPresent() ? -p.second : p.second);
    ParserCombinator<List<Pair<String, Integer>>> restOfTheTerms =
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

    ParserCombinator<List<Pair<String, Integer>>> restOfTheFactors =
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

    ParserCombinator<Integer> parenthesizedExpression =
        skip(string("(")).next(expression).skip(string(")"));

    factor.setCombinator(parseInteger.or(parenthesizedExpression));

    parser = expression.end();
  }

  @Override
  public Integer apply(String input) {
    return parser.apply(input);
  }

}
