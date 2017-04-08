package org.jparsercombinator.examples.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.jparsercombinator.Parser;

public class QueryParserTest {

  private Parser<Query> queryParser = new QueryParser();

  @Test
  public void shouldParseQueries() {
    assertEquals("(())",
        queryParser.apply("").toString());

    assertEquals("((name = John))",
        queryParser.apply("name:John").toString());

    assertEquals("(((NOT name = John)))",
        queryParser.apply("NOT name:John").toString());

    assertEquals("(((NOT name = John) AND name = Jack))",
        queryParser.apply("NOT name:John AND name:Jack").toString());

    assertEquals("(((NOT ((name = John AND name = Jack)))))",
        queryParser.apply("NOT (name:John AND name:Jack)").toString());

    assertEquals("(((NOT name = John) AND (NOT name = Jack)))",
        queryParser.apply("NOT name:John AND NOT name:Jack").toString());

    assertEquals("((name = John) OR (name = Beck))",
        queryParser.apply("name:John OR name:Beck").toString());

    assertEquals("((name = John AND age = 23))",
        queryParser.apply("name:John AND age:23").toString());

    assertEquals("((name = John AND age = 23) OR (name = Beck AND age = 77))",
        queryParser.apply("name:John AND age:23 OR name:Beck AND age:77").toString());

    assertEquals("((((name = John AND age = 23))) OR (((name = Beck AND age = 77))))",
        queryParser.apply("(name:John AND age:23) OR (name:Beck AND age:77)").toString());

    assertEquals("((name = John AND age = 23) OR (name = Beck) OR (age = 77 AND name = John))",
        queryParser.apply("name:John AND age:23 OR name:Beck OR age:77 AND name:John").toString());
  }

}
