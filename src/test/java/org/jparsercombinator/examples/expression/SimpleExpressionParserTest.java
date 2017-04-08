package org.jparsercombinator.examples.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.jparsercombinator.Parser;

public class SimpleExpressionParserTest {

  private Parser<Integer> expressionParser = new SimpleExpressionParser();

  @Test
  public void shouldParseExpressions() {
    assertEquals(123, expressionParser.apply("123").intValue());
    assertEquals(12, expressionParser.apply("(2*(2+4))").intValue());
    assertEquals(5, expressionParser.apply("(10/(1+1))").intValue());
    assertEquals(10, expressionParser.apply("(2+((2+2)*2))").intValue());
    assertEquals(-3, expressionParser.apply("(2-5)").intValue());
  }

}
