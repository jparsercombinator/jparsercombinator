package org.jparsercombinator.examples.expression;

import static org.jparsercombinator.Combinators.newRef;
import static org.jparsercombinator.Combinators.regex;
import static org.jparsercombinator.Combinators.skip;
import static org.jparsercombinator.Combinators.string;

import org.jparsercombinator.Combinator;
import org.jparsercombinator.CombinatorReference;
import org.jparsercombinator.Parser;
import org.jparsercombinator.Parsers;

/**
 * Parser for parsing and evaluating of "fully parenthesized expressions":
 *
 * <expression> ::= <integer> | ( "(" <expression> ( "+" | "-" | "*" | "/" ) <expression> ")" )
 */
class SimpleExpressionParser implements Parser<Integer> {

  private Parser<Integer> parser;

  SimpleExpressionParser() {
    Combinator<Integer> parseInteger = regex("[0-9]+").map(Integer::parseInt);
    Combinator<String> parseOperator = regex("(\\+|\\-|\\*|\\/)");

    // need to be defined as reference to avoid illegal self reference
    CombinatorReference<Integer> parseExpression = newRef();

    parseExpression.setCombinator(
        parseInteger.or(
            skip(string("("))
                .next(parseExpression)
                .next(parseOperator)
                .next(parseExpression)
                .skip(string(")"))
                .map(data -> {
                  switch (data.first.second) {
                    case "+": return data.first.first + data.second;
                    case "-": return data.first.first - data.second;
                    case "*": return data.first.first * data.second;
                    case "/": return data.first.first / data.second;
                    default: throw new IllegalStateException();
                  }
                })));

    parser = Parsers.parser(parseExpression);
  }

  @Override
  public Integer apply(String input) {
    return parser.apply(input);
  }

}
