package org.jparsercombinator;

import static org.jparsercombinator.Combinators.string;
import static org.jparsercombinator.Parsers.parser;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class ErrorReportingParserTest {

  @Test(expected = ParseException.class)
  public void shouldThrowParseExceptionOnRejectedInput() {
    Parser<String> fooParser = parser(string("foo"));

    fooParser.apply("bar");
  }

  @Test(expected = ParseException.class)
  public void shouldThrowParseExceptionOnIncompleteParse() {
    Parser<String> fooParser = parser(string("foo"));

    fooParser.apply("foobar");
  }

  @Test
  public void shouldReturnEmptyOptionalAndSupplyErrorMessageOnRejectedInput() {
    List<String> errorMessages = new ArrayList<>();
    Parser<Optional<String>> fooParser = parser(string("foo"), errorMessages::add);

    assertTrue(errorMessages.isEmpty());

    Optional<String> result = fooParser.apply("bar");

    assertTrue(!result.isPresent());
    assertTrue(!errorMessages.isEmpty());
  }

  @Test
  public void shouldReturnEmptyOptionalAndSupplyErrorMessageOnIncompleteParse() {
    List<String> errorMessages = new ArrayList<>();
    Parser<Optional<String>> fooParser = parser(string("foo"), errorMessages::add);

    assertTrue(errorMessages.isEmpty());

    Optional<String> result = fooParser.apply("foobar");

    assertTrue(!result.isPresent());
    assertTrue(!errorMessages.isEmpty());
  }

}
