package org.jparsercombinator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.jparsercombinator.ParserCombinators.regex;
import static org.jparsercombinator.ParserCombinators.string;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class ParserCombinatorTest {

  private ParserCombinator<String> foo = string("foo");
  private ParserCombinator<String> bar = string("bar");

  private Parser<String> fooParser = foo.end();
  private Parser<Optional<String>> fooOptionalParser = foo.optional().end();
  private Parser<String> fooOrBarParser = foo.or(bar).end();
  private Parser<List<String>> fooRepeatingParser = foo.many().end();
  private Parser<List<String>> fooRepeatingCommaSeparatedParser = foo.many(string(",")).end();
  private Parser<Pair<String, String>> fooNextBarParser = foo.next(bar).end();
  private Parser<Integer> integerParser = regex("[0-9]+").map(Integer::parseInt).end();

  @Test
  public void shouldParseConstantString() {
    assertEquals("foo", fooParser.apply("foo"));
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseWrongConstant() {
    fooParser.apply("bar");
  }

  @Test
  public void shouldParseOptionalConstant() {
    assertEquals(Optional.of("foo"), fooOptionalParser.apply("foo"));
    assertEquals(Optional.empty(), fooOptionalParser.apply(""));
  }

  @Test
  public void shouldParseAnyPossibleConstant() {
    assertEquals("foo", fooOrBarParser.apply("foo"));
    assertEquals("bar", fooOrBarParser.apply("bar"));
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseImpossibleConstants() {
    fooOrBarParser.apply("baz");
  }

  @Test
  public void shouldParseSequenceOfConstants() {
    assertEquals(new Pair<>("foo", "bar"), fooNextBarParser.apply("foobar"));
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseWrongSequenceOfConstants() {
    fooNextBarParser.apply("foofoobar");
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseUnknownConstant() {
    fooOrBarParser.apply("baz");
  }

  @Test
  public void shouldParseRepeatedConstantWithZeroValues() {
    assertEquals(emptyList(), fooRepeatingParser.apply(""));
  }

  @Test
  public void shouldParseRepeatedConstantWithOneValue() {
    assertEquals(singletonList("foo"), fooRepeatingParser.apply("foo"));
  }

  @Test
  public void shouldParseRepeatedConstant() {
    assertEquals(Arrays.asList("foo", "foo", "foo"), fooRepeatingParser.apply("foofoofoo"));
  }

  @Test
  public void shouldParseLongRepeatedConstant() {
    int repeats = 1000;

    String input = String.join("", Collections.nCopies(repeats, "foo"));

    List<String> result = fooRepeatingParser.apply(input);
    assertEquals(repeats, result.size());
    result.forEach(r -> assertEquals("foo", r));
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseRepeatedConstantWithExtraValues() {
    fooRepeatingParser.apply("foofoofoobar");
  }

  @Test
  public void shouldParseRepeatedCommaSeparatedConstant() {
    assertEquals(Arrays.asList("foo", "foo", "foo"),
        fooRepeatingCommaSeparatedParser.apply("foo,foo,foo"));
  }

  @Test
  public void shouldParseLongRepeatedCommaSeparatedConstant() {
    int repeats = 1000;

    String input = String.join(",", Collections.nCopies(repeats, "foo"));

    List<String> result = fooRepeatingCommaSeparatedParser.apply(input);
    assertEquals(repeats, result.size());
    result.forEach(r -> assertEquals("foo", r));
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseRepeatedCommaSeparatedWithExtraCommas() {
    fooRepeatingCommaSeparatedParser.apply("foo,foo,,foo");
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseRepeatedCommaSeparatedWithExtraTrailingComma() {
    fooRepeatingCommaSeparatedParser.apply("foo,foo,foo,");
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseRepeatedCommaSeparatedWithExtraLeadingComma() {
    fooRepeatingCommaSeparatedParser.apply(",foo,foo,foo");
  }

  @Test
  public void shouldParseInteger() {
    assertEquals(23, integerParser.apply("23").intValue());
  }

  @Test(expected = ParseException.class)
  public void shouldNotParseNonInteger() {
    integerParser.apply("23.2");
  }

}