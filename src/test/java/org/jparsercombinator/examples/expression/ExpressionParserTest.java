package org.jparsercombinator.examples.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.jparsercombinator.ParseException;
import org.jparsercombinator.Parser;

public class ExpressionParserTest {

  private Parser<Integer> expressionParser = new ExpressionParser();

  @Test(expected = ParseException.class)
  public void shouldFailOnEmptyInput() {
    expressionParser.apply("");
  }

  @Test
  public void shouldParseInteger() {
    assertEquals(23, expressionParser.apply("23").intValue());
    assertEquals(23, expressionParser.apply("(23)").intValue());
    assertEquals(23, expressionParser.apply("((23))").intValue());
  }

  @Test
  public void shouldParseSimpleExpressions() {
    assertEquals(123, expressionParser.apply("123").intValue());
    assertEquals(12, expressionParser.apply("(2*(2+4))").intValue());
    assertEquals(5, expressionParser.apply("(10/(1+1))").intValue());
    assertEquals(10, expressionParser.apply("(2+((2+2)*2))").intValue());
    assertEquals(-3, expressionParser.apply("(2-5)").intValue());
  }

  @Test
  public void shouldParseMoreExpressions() {
    assertEquals(9, expressionParser.apply("2+3+4").intValue());
    assertEquals(13, expressionParser.apply("2+3+4+5-1").intValue());
    assertEquals(8, expressionParser.apply("2*2+4").intValue());
    assertEquals(0, expressionParser.apply("-2*2+4").intValue());
    assertEquals(0, expressionParser.apply("(-2*2)+4").intValue());
    assertEquals(8, expressionParser.apply("-(-2*2)+4").intValue());
    assertEquals(-12, expressionParser.apply("-2*(2+4)").intValue());
  }

  @Test(expected = ParseException.class)
  public void shouldFailOnMissingParen() {
    expressionParser.apply("2+(2*");
  }

  @Test(expected = ParseException.class)
  public void shouldFailOnUnknownChars() {
    expressionParser.apply("2~2");
  }

}
