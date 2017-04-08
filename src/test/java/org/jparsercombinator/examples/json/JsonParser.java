package org.jparsercombinator.examples.json;


import static org.jparsercombinator.Combinators.newRef;
import static org.jparsercombinator.Combinators.regex;
import static org.jparsercombinator.Combinators.regexFullResult;
import static org.jparsercombinator.Combinators.skip;
import static org.jparsercombinator.Combinators.string;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.jparsercombinator.Combinator;
import org.jparsercombinator.CombinatorReference;
import org.jparsercombinator.Parser;
import org.jparsercombinator.Parsers;

/**
 * Test parsing JSON primitives and objects. Note that the parser doesn't conform to full JSON
 * specification.
 */
class JsonParser implements Parser<JsonElement> {

  private Parser<JsonElement> parser;

  JsonParser() {
    CombinatorReference<JsonElement> jsonParser = newRef();

    Combinator<JsonElement> jsonNullParser = string("null").map(v -> new JsonNull());

    Combinator<JsonElement> jsonBooleanParser =
        regex("(true|false)").map(v -> new JsonPrimitive(Boolean.valueOf(v)));
    Combinator<JsonElement> jsonIntegerParser =
        regex("[0-9]+").map(v -> new JsonPrimitive(Integer.parseInt(v)));
    Combinator<JsonElement> jsonStringParser =
        regexFullResult("\"([^\"]*)\"").map(v -> new JsonPrimitive(v.group(1)));
    Combinator<JsonElement> jsonPrimitiveParser =
        jsonStringParser.or(jsonBooleanParser).or(jsonIntegerParser);

    CombinatorReference<JsonElement> jsonObjectParser = newRef();
    CombinatorReference<JsonElement> jsonArrayParser = newRef();

    Combinator<Entry<String, JsonElement>> jsonObjectEntryParser =
        jsonStringParser.skip(string(":")).next(jsonParser)
            .map(pair -> new SimpleEntry<>(pair.first.toString(), pair.second));

    jsonObjectParser.setCombinator(
        skip(string("{"))
            .next(jsonObjectEntryParser.many(string(",")))
            .skip(string("}"))
            .map(JsonObject::new));

    jsonArrayParser.setCombinator(
        skip(string("["))
            .next(jsonParser.many(string(",")))
            .skip(string("]"))
            .map(JsonArray::new));

    jsonParser.setCombinator(
        jsonNullParser.or(jsonPrimitiveParser).or(jsonArrayParser).or(jsonObjectParser));

    parser = Parsers.parser(jsonParser);
  }

  @Override
  public JsonElement apply(String input) {
    return parser.apply(input);
  }

}
