package org.jparsercombinator.examples;

import static org.jparsercombinator.Combinators.*;
import static org.jparsercombinator.Parsers.parser;

import java.util.List;
import org.jparsercombinator.*;

import org.junit.Test;

public class SimpleExamples {

  @Test(expected = ParseException.class)
  public void example0() {

    Parser<String> fooParser = parser(string("foo"));

    fooParser.apply("foo");  // returns "foo"
    fooParser.apply("bar");  // throws ParseException

  }

  @Test(expected = ParseException.class)
  public void example1() {

    Combinator<String> fooParserCombinator = string("foo");

    // apply and get the parse result, in this case "Accept" with result string and remaining input
    Result<String> result = fooParserCombinator.apply("foo");
    result.isAccepted();      // true
    result.result();          // "foo"
    result.remainingInput();  // ""

    Result<String> rejected = fooParserCombinator.apply("bar");
    rejected.isAccepted();    // false
    rejected.errorMessage();  // ...

    // For simpler end use, we can wrap Combinator with Parser.parser, e.g.
    Parser<String> fooParser = parser(fooParserCombinator);
    fooParser.apply("foo");  // "foo"
    fooParser.apply("bar");  // throws ParseException

  }

  @Test(expected = ParseException.class)
  public void example2() {

    Parser<String> fooOrBarParser = parser(string("foo").or(string("bar")));

    fooOrBarParser.apply("foo");  // returns "foo"
    fooOrBarParser.apply("bar");  // returns "bar"
    fooOrBarParser.apply("baz");  // throws ParseException

  }

  @Test(expected = ParseException.class)
  public void example3() {

    Parser<Pair<String, String>> fooAndBarParser = parser(string("foo").next(string("bar")));

    fooAndBarParser.apply("foobar");  // returns ("foo", "bar")
    fooAndBarParser.apply("foo");     // throws ParseException

  }

  @Test(expected = ParseException.class)
  public void example4() {

    Parser<Integer> integerParser = parser(regex("[0-9]+").map(Integer::parseInt));

    integerParser.apply("4");    // returns 4
    integerParser.apply("foo");  // throws ParseException

  }

  @Test(expected = ParseException.class)
  public void example5() {

    Parser<List<String>> fooManyParser = parser(string("foo").many());

    fooManyParser.apply("foofoofoo");  // returns ["foo", "foo", "foo"]
    fooManyParser.apply("foobarfoo");  // throws ParseException

  }

  @Test(expected = ParseException.class)
  public void example6() {

    // example of a recursive parser
    CombinatorReference<String> parenCombinator = newRef();
    parenCombinator.setCombinator(skip(string("(")).next(parenCombinator).skip(string(")")));

    Parser<String> parenParser = parser(parenCombinator);

    parenParser.apply("()");     // ""
    parenParser.apply("(())");   // ""
    parenParser.apply("((()))"); // ""
    parenParser.apply("((()");   // throws ParseException

  }

  @Test
  public void example7() {

    // evaluates simple "fully parenthesized" expressions
    Combinator<Integer> parseInteger = regex("[0-9]+").map(Integer::parseInt);
    Combinator<String> parseOperator = regex("(\\+|\\-|\\*|\\/)");
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

    Parser<Integer> calculator = Parsers.parser(parseExpression);

    calculator.apply("4");          // 4
    calculator.apply("(1+1)");      // 2
    calculator.apply("(2*3)");      // 6
    calculator.apply("(1+(3*3))");  // 10
    calculator.apply("((4/2)*2)");  // 4

  }

}
