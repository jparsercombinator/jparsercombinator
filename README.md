# jparsercombinator

Simple parser combinator written in Java 1.8.

## Usage examples

Imports in examples:
```java
import org.jparsercombinator.*;
import static org.jparsercombinator.ParserCombinators.*;
```

Simplest parser recognizes constant string and accepts inputs matching it.

```java
Parser<String> fooParser = string("foo").end();

fooParser.apply("foo");  // returns "foo"
fooParser.apply("bar");  // throws ParseException
```

Parser accepting alternatives is defined as follows.

```java
Parser<String> fooOrBarParser = string("foo").or(string("bar")).end();

fooOrBarParser.apply("foo");  // returns "foo"
fooOrBarParser.apply("bar");  // returns "bar"
fooOrBarParser.apply("baz");  // throws ParseException
```

Parser accepting sequences is defined as follows.

```java
Parser<Pair<String, String>> fooAndBarParser = string("foo").next(string("bar")).end();

fooAndBarParser.apply("foobar");  // returns ("foo", "bar")
fooAndBarParser.apply("foo");     // throws ParseException
```

Parse result can be mapped with given function.

```java
Parser<Integer> integerParser = regex("[0-9]+").map(Integer::parseInt).end();

integerParser.apply("4");    // returns 4
integerParser.apply("foo");  // throws ParseException
```

Repeated parser recognizes multiple values and returns a list.

```java
Parser<List<String>> fooManyParser = string("foo").many().end();

fooManyParser.apply("foofoofoo");  // returns ["foo", "foo", "foo"]
fooManyParser.apply("foobarfoo");  // throws ParseException
```

## Key concepts

Parser is simply defined as

```java
public interface Parser<T> {

  T apply(String input);

}
```

Parser reads some input string and returns a parse result. The result may simply be the recognized 
string or some more complex structure such as abstract syntax tree (AST).
  
Parser combinator is defined as a parser which returns a Result object (either Accept or Reject).

```java
public interface ParserCombinator<T> extends Parser<Result<T>> {

  // ...

}
```

Accept contains parse result and remaining input string. Reject object signals failed parse 
and contains only an error message. 

ParserCombinator also defines various default methods for combining it with other parse combinators
to produce more complex parsers.

```java
public interface ParserCombinator<T> extends Parser<Result<T>> {
  
  default Combinator<T> or(Combinator<T> or) {
    return new CombinatorOr<>(this, or);
  }
  
  // ...
  
}
```

Base parsers can be created with static factory methods of ParserCombinators class.

```java
// from org.jparsercombinator.ParserCombinators.*;
ParserCombinator<String> fooParser = string("foo");    // accepts "foo"
ParserCombinator<String> intParser = regex("[0-9]+");  // accepts integers

ParserCombinator<String> fooOrIntParser = string("foo").or(regex("[0-9]+")); // "foo" or integers
```

## More complex examples

Self referencing parsers may be defined using delegated reference.

```java
// example of a recursive parser
ParserCombinatorReference<String> parenCombinator = newRef();
parenCombinator.setCombinator(skip(string("(")).next(parenCombinator).skip(string(")")));

Parser<String> parenParser = parenCombinator.end();

parenParser.apply("()");     // ""
parenParser.apply("(())");   // ""
parenParser.apply("((()))"); // ""
parenParser.apply("((()");   // throws ParseException
```

Finally, a more complex example shows simple parser for evaluating "fully parenthesized" expressions.

```java
ParserCombinator<Integer> parseInteger = regex("[0-9]+").map(Integer::parseInt);
ParserCombinator<String> parseOperator = regex("(\\+|\\-|\\*|\\/)");
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

Parser<Integer> calculator = parseExpression.end();

calculator.apply("4");          // 4
calculator.apply("(1+1)");      // 2
calculator.apply("(2*3)");      // 6
calculator.apply("(1+(3*3))");  // 10
calculator.apply("((4/2)*2)");  // 4
```

## Error handling

Parser combinator reads a string value and returns a Result object which is instance of either
Accept or Reject. Accept contains parse result and remaining input string. Reject contains only
an error message.

```java
ParserCombinator<String> fooParserCombinator = string("foo");

// apply and get the parse result, in this case "Accept" with result string and remaining input
Result<String> accepted = fooParserCombinator.apply("foo");
accepted.isAccepted();      // true
accepted.result();          // "foo"
accepted.remainingInput();  // ""

Result<String> rejected = fooParserCombinator.apply("bar");
rejected.isAccepted();      // false
rejected.errorMessage();    // ...
```

For simpler use, a parser combinator is typically closed with an end method.

```java
Parser<String> exceptionThrowingFooParser = fooParserCombinator.end();
exceptionThrowingFooParser.apply("foo");  // "foo"
exceptionThrowingFooParser.apply("bar");  // throws ParseException
```
