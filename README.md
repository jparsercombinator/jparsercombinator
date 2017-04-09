# jparsercombinator

Simple parser combinator written in Java.

Requires JDK 1.8 or higher.

## Usage examples

Imports in examples:
```java
import static org.jparsercombinator.Combinators.*;
import static org.jparsercombinator.Parsers.parser;
import org.jparsercombinator.*;
```

Simplest parser recognizes constant string and accepts inputs matching it.

```java
Parser<String> fooParser = parser(string("foo"));

fooParser.apply("foo");  // returns "foo"
fooParser.apply("bar");  // throws ParseException
```

Parser accepting alternatives is defined as follows.

```java
Parser<String> fooOrBarParser = parser(string("foo").or(string("bar")));

fooOrBarParser.apply("foo");  // returns "foo"
fooOrBarParser.apply("bar");  // returns "bar"
fooOrBarParser.apply("baz");  // throws ParseException
```

Parser accepting sequences is defined as follows.

```java
Parser<Pair<String, String>> fooAndBarParser = parser(string("foo").next(string("bar")));

fooAndBarParser.apply("foobar");  // returns ("foo", "bar")
fooAndBarParser.apply("foo");     // throws ParseException
```

Parse result can be mapped with given function.

```java
Parser<Integer> integerParser = parser(regex("[0-9]+").map(Integer::parseInt));

integerParser.apply("4");    // returns 4
integerParser.apply("foo");  // throws ParseException
```

Repeated parser recognizes multiple values and returns a list.

```java
Parser<List<String>> fooManyParser = parser(string("foo").many());

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
public interface Combinator<T> extends Parser<Result<T>> {
  // ...
}
```

Accept contains parse result and remaining input string. Reject object signals failed parse 
and contains only an error message. 

Combinator also defines various default methods for combining it with other parse combinators to 
produce more complex parsers.

```java
public interface Combinator<T> extends Parser<Result<T>> {
  
  default Combinator<T> or(Combinator<T> or) {
    return new CombinatorOr<>(this, or);
  }
  
  // ...
  
}
```

Base parsers can be created with static factory methods of Combinators class.

```java
// from org.jparsercombinator.Combinators.*;
Combinator<String> fooParser = string("foo");    // accepts "foo"
Combinator<String> intParser = regex("[0-9]+");  // accepts integers

Combinator<String> fooOrIntParser = string("foo").or(regex("[0-9]+")); // accepts "foo" or integers
```

## More complex examples

Self referencing parsers may be defined using delegated reference.

```java
// example of a recursive parser
CombinatorReference<String> parenCombinator = newRef();

parenCombinator.setCombinator(skip(string("(")).next(parenCombinator).skip(string(")")));

Parser<String> parenParser = parser(parenCombinator);
parenParser.apply("()");     // ""
parenParser.apply("(())");   // ""
parenParser.apply("((()))"); // ""
parenParser.apply("((()");   // throws ParseException
```

Finally, a more complex example shows simple parser for evaluating "fully parenthesized" expressions.

```java
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
```

## Error handling

Parser combinator (Combinator) reads a string value and returns a Result object which is instance 
of either Accept or Reject. Accept contains parse result and remaining input string. Reject contains 
only an error message.

```java
Combinator<String> fooParserCombinator = string("foo");

// apply and get the parse result, in this case "Accept" with result string and remaining input
Result<String> accepted = fooParserCombinator.apply("foo");
accepted.isAccepted();     // true
accepted.result();         // "foo"
accepted.remainingInput(); // ""

Result<String> rejected = fooParserCombinator.apply("bar");
rejected.isAccepted();     // false
rejected.errorMessage();   // ...
```

For simpler Exception based error handling (as in the examples), we can wrap Combinator with 
Parsers.parser(...)

```java
Parser<String> exceptionThrowingFooParser = parser(fooParserCombinator);
exceptionThrowingFooParser.apply("foo");  // "foo"
exceptionThrowingFooParser.apply("bar");  // throws ParseException
```

Another alternative is an optional returning parser with an error message consumer

```java
Parser<Optional<String>> optionalReturningFooParser = parser(fooParserCombinator, 
    System.err::println);
optionalReturningFooParser.apply("foo");  // Optional[foo]
optionalReturningFooParser.apply("bar");  // Optional.empty (an error message is also printed to System.err)
```
