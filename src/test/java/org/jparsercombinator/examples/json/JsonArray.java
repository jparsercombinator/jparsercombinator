package org.jparsercombinator.examples.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class JsonArray implements JsonElement {

  private List<JsonElement> elements = new ArrayList<>();

  JsonArray(JsonElement... elements) {
    this(Arrays.asList(elements));
  }

  JsonArray(List<JsonElement> elements) {
    this.elements.addAll(elements);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonArray jsonArray = (JsonArray) o;
    return Objects.equals(elements, jsonArray.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }

}
