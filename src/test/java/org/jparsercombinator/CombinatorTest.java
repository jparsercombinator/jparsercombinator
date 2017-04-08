package org.jparsercombinator;

import static org.jparsercombinator.Combinators.regex;
import static org.jparsercombinator.Combinators.string;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class CombinatorTest {

  private Combinator<String> fooParser = string("foo");
  private Combinator<String> barParser = string("bar");
  private Combinator<Optional<String>> fooOptionalParser = fooParser.optional();
  private Combinator<String> fooOrBarParser = fooParser.or(barParser);
  private Combinator<List<String>> fooRepeatingParser = fooParser.many();
  private Combinator<List<String>> fooRepeatingCommaSeparatedParser = fooParser.many(string(","));
  private Combinator<Pair<String, String>> fooNextBarParser = fooParser.next(barParser);
  private Combinator<Integer> integerParser = regex("[0-9]+").map(Integer::parseInt);

  @Test
  public void shouldParseConstantString() {
    assertEquals("foo", fooParser.apply("foo").result());
  }

  @Test
  public void shouldNotAcceptWrongConstant() {
    assertFalse(fooParser.apply("bar").isAccepted());
  }

  @Test
  public void shouldParseOptionalConstant() {
    assertEquals(Optional.of("foo"), fooOptionalParser.apply("foo").result());
    assertEquals(Optional.empty(), fooOptionalParser.apply("bar").result());
  }

  @Test
  public void shouldParseAnyPossibleConstant() {
    assertEquals("foo", fooOrBarParser.apply("foo").result());
    assertEquals("bar", fooOrBarParser.apply("bar").result());
  }

  @Test
  public void shouldParseSequenceOfConstants() {
    assertEquals(new Pair<>("foo", "bar"), fooNextBarParser.apply("foobar").result());
  }

  @Test
  public void shouldNotAcceptUnknownConstant() {
    assertFalse(fooOrBarParser.apply("baz").isAccepted());
  }

  @Test
  public void shouldParseRepeatedConstant() {
    assertEquals(Arrays.asList("foo", "foo", "foo"),
        fooRepeatingParser.apply("foofoofoo").result());
  }

  @Test
  public void shouldParseRepeatedCommaSeparatedConstant() {
    assertEquals(Arrays.asList("foo", "foo", "foo"),
        fooRepeatingCommaSeparatedParser.apply("foo,foo,foo").result());
  }

  @Test
  public void shouldParseInteger() {
    assertEquals(23, integerParser.apply("23").result().intValue());
  }

}