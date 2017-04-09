package org.jparsercombinator.examples.json;


import static org.jparsercombinator.ParserCombinators.newRef;
import static org.jparsercombinator.ParserCombinators.regex;
import static org.jparsercombinator.ParserCombinators.regexFullResult;
import static org.jparsercombinator.ParserCombinators.skip;
import static org.jparsercombinator.ParserCombinators.string;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.jparsercombinator.ParserCombinator;
import org.jparsercombinator.ParserCombinatorReference;
import org.jparsercombinator.Parser;

/**
 * Test parsing JSON primitives and objects. Note that the parser doesn't conform to full JSON
 * specification.
 */
class JsonParser implements Parser<JsonElement> {

  private Parser<JsonElement> parser;

  JsonParser() {
    ParserCombinatorReference<JsonElement> jsonParser = newRef();

    ParserCombinator<JsonElement> jsonNullParser = string("null").map(v -> new JsonNull());

    ParserCombinator<JsonElement> jsonBooleanParser =
        regex("(true|false)").map(v -> new JsonPrimitive(Boolean.valueOf(v)));
    ParserCombinator<JsonElement> jsonIntegerParser =
        regex("[0-9]+").map(v -> new JsonPrimitive(Integer.parseInt(v)));
    ParserCombinator<JsonElement> jsonStringParser =
        regexFullResult("\"([^\"]*)\"").map(v -> new JsonPrimitive(v.group(1)));
    ParserCombinator<JsonElement> jsonPrimitiveParser =
        jsonStringParser.or(jsonBooleanParser).or(jsonIntegerParser);

    ParserCombinatorReference<JsonElement> jsonObjectParser = newRef();
    ParserCombinatorReference<JsonElement> jsonArrayParser = newRef();

    ParserCombinator<Entry<String, JsonElement>> jsonObjectEntryParser =
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

    parser = jsonParser.end();
  }

  @Override
  public JsonElement apply(String input) {
    return parser.apply(input);
  }

}
