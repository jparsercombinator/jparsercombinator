package org.jparsercombinator.examples.expression;

import static org.jparsercombinator.ParserCombinators.newRef;
import static org.jparsercombinator.ParserCombinators.regex;
import static org.jparsercombinator.ParserCombinators.skip;
import static org.jparsercombinator.ParserCombinators.string;

import org.jparsercombinator.ParserCombinator;
import org.jparsercombinator.ParserCombinatorReference;
import org.jparsercombinator.Parser;

/**
 * Parser for parsing and evaluating of "fully parenthesized expressions":
 *
 * <expression> ::= <integer> | ( "(" <expression> ( "+" | "-" | "*" | "/" ) <expression> ")" )
 */
class SimpleExpressionParser implements Parser<Integer> {

  private Parser<Integer> parser;

  SimpleExpressionParser() {
    ParserCombinator<Integer> parseInteger = regex("[0-9]+").map(Integer::parseInt);
    ParserCombinator<String> parseOperator = regex("(\\+|\\-|\\*|\\/)");

    // need to be defined as reference to avoid illegal self reference
    ParserCombinatorReference<Integer> parseExpression = newRef();

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

    parser = parseExpression.end();
  }

  @Override
  public Integer apply(String input) {
    return parser.apply(input);
  }

}
