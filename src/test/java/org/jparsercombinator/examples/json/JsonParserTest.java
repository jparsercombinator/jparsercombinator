package org.jparsercombinator.examples.json;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap.SimpleEntry;
import org.jparsercombinator.Parser;
import org.junit.Test;

public class JsonParserTest {

  private Parser<JsonElement> jsonParser = new JsonParser();

  @Test
  public void shouldParseJsonNull() {
    assertEquals(new JsonNull(), jsonParser.apply("null"));
  }

  @Test
  public void shouldParseJsonBooleanPrimitive() {
    assertEquals(new JsonPrimitive(true), jsonParser.apply("true"));
    assertEquals(new JsonPrimitive(false), jsonParser.apply("false"));
  }

  @Test
  public void shouldParseJsonStringPrimitive() {
    assertEquals(new JsonPrimitive("bar"), jsonParser.apply("\"bar\""));
  }

  @Test
  public void shouldParseJsonNumberPrimitive() {
    assertEquals(new JsonPrimitive(12), jsonParser.apply("12"));
  }

  @Test
  public void shouldParseEmptyObject() {
    JsonObject jsonObject = new JsonObject();

    assertEquals(jsonObject, jsonParser.apply("{}"));
  }

  @Test
  public void shouldParseSimpleJsonObject() {
    JsonObject jsonObject = new JsonObject(
        new SimpleEntry<>("foo", new JsonPrimitive("bar")));

    assertEquals(jsonObject, jsonParser.apply("{\"foo\":\"bar\"}"));
  }

  @Test
  public void shouldParseJsonObjectWithMultipleFields() {
    JsonObject jsonObject = new JsonObject(
        new SimpleEntry<>("name", new JsonPrimitive("John")),
        new SimpleEntry<>("age", new JsonPrimitive(23)),
        new SimpleEntry<>("active", new JsonPrimitive(true)));

    assertEquals(jsonObject, jsonParser.apply("{\"name\":\"John\",\"age\":23,\"active\":true}"));
  }

  @Test
  public void shouldParseSimpleNestedJsonObject() {
    JsonObject jsonObject = new JsonObject(
        new SimpleEntry<>("foo", new JsonObject(
            new SimpleEntry<>("bar", new JsonPrimitive("baz")))));

    assertEquals(jsonObject, jsonParser.apply("{\"foo\":{\"bar\":\"baz\"}}"));
  }

  @Test
  public void shouldParseEmptyArray() {
    JsonArray jsonArray = new JsonArray();

    assertEquals(jsonArray, jsonParser.apply("[]"));
  }

  @Test
  public void shouldParseSimpleArray() {
    JsonArray jsonArray = new JsonArray(
        new JsonPrimitive(true),
        new JsonPrimitive(false),
        new JsonPrimitive(123));

    assertEquals(jsonArray, jsonParser.apply("[true,false,123]"));
  }

  @Test
  public void shouldParseNestedArray() {
    JsonArray jsonArray = new JsonArray(
        new JsonArray(
            new JsonPrimitive(1),
            new JsonPrimitive(2)),
        new JsonArray(
            new JsonPrimitive(3),
            new JsonPrimitive(4)));

    assertEquals(jsonArray, jsonParser.apply("[[1,2],[3,4]]"));
  }

  @Test
  public void shouldParseNestedArraysAndObjects() {
    JsonArray firstArray =
        new JsonArray(
            new JsonObject(new SimpleEntry<>("1", new JsonArray(
                new JsonObject(new SimpleEntry<>("1", new JsonPrimitive(1))),
                new JsonObject(new SimpleEntry<>("2", new JsonPrimitive(2)))))),
            new JsonObject(new SimpleEntry<>("2", new JsonArray(
                new JsonObject(new SimpleEntry<>("1", new JsonPrimitive(3))),
                new JsonObject(new SimpleEntry<>("2", new JsonPrimitive(4)))))));

    JsonArray secondArray =
        new JsonArray(
            new JsonObject(new SimpleEntry<>("1", new JsonArray(
                new JsonObject(new SimpleEntry<>("1", new JsonPrimitive(5))),
                new JsonObject(new SimpleEntry<>("2", new JsonPrimitive(6)))))),
            new JsonObject(new SimpleEntry<>("2", new JsonArray(
                new JsonObject(new SimpleEntry<>("1", new JsonPrimitive(7))),
                new JsonObject(new SimpleEntry<>("2", new JsonPrimitive(8)))))));

    JsonObject jsonObject = new JsonObject(
        new SimpleEntry<>("1", firstArray),
        new SimpleEntry<>("2", secondArray));

    String firstArrayJson = "[{\"1\":[{\"1\":1},{\"2\":2}]},{\"2\":[{\"1\":3},{\"2\":4}]}]";
    String secondArrayJson = "[{\"1\":[{\"1\":5},{\"2\":6}]},{\"2\":[{\"1\":7},{\"2\":8}]}]";
    String json = String.format("{\"1\":%s,\"2\":%s}", firstArrayJson, secondArrayJson);

    assertEquals(jsonObject, jsonParser.apply(json));
  }

  @Test
  public void shouldParseComplexObject() {
    JsonObject jsonObject = new JsonObject(
        new SimpleEntry<>("foo", new JsonObject(
            new SimpleEntry<>("bar", new JsonObject(
                new SimpleEntry<>("baz", new JsonPrimitive(true)))))),
        new SimpleEntry<>("data",
            new JsonArray(
                new JsonArray(
                    new JsonPrimitive(1),
                    new JsonPrimitive(2)),
                new JsonArray(
                    new JsonPrimitive(3),
                    new JsonPrimitive(4)))));

    assertEquals(jsonObject,
        jsonParser.apply("{\"foo\":{\"bar\":{\"baz\":true}},\"data\":[[1,2],[3,4]]}"));
  }

}
