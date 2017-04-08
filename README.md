# jparsercombinator
Simple parser combinator written in Java

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

Parser combinator reads string values and returns Result objects which are instances of either
Accept or Reject classes. Accept contains parsed result with remaining input string. Reject contains 
only an error message.

```java
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
