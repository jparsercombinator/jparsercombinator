package org.jparsercombinator;

import static org.jparsercombinator.ParserCombinators.regex;
import static org.jparsercombinator.ParserCombinators.string;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
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
  public void shouldNotAcceptWrongConstant() {
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

  @Test
  public void shouldParseSequenceOfConstants() {
    assertEquals(new Pair<>("foo", "bar"), fooNextBarParser.apply("foobar"));
  }

  @Test(expected = ParseException.class)
  public void shouldNotAcceptUnknownConstant() {
    fooOrBarParser.apply("baz");
  }

  @Test
  public void shouldParseRepeatedConstant() {
    assertEquals(Arrays.asList("foo", "foo", "foo"), fooRepeatingParser.apply("foofoofoo"));
  }

  @Test
  public void shouldParseRepeatedCommaSeparatedConstant() {
    assertEquals(Arrays.asList("foo", "foo", "foo"),
        fooRepeatingCommaSeparatedParser.apply("foo,foo,foo"));
  }

  @Test
  public void shouldParseInteger() {
    assertEquals(23, integerParser.apply("23").intValue());
  }

}