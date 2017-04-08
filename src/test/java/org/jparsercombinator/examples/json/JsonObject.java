package org.jparsercombinator.examples.json;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

class JsonObject implements JsonElement {

  private Map<String, JsonElement> members = new LinkedHashMap<>();

  @SafeVarargs
  JsonObject(Map.Entry<String, JsonElement>... entries) {
    this(Arrays.asList(entries));
  }

  JsonObject(List<Entry<String, JsonElement>> entries) {
    entries.forEach(entry -> members.put(entry.getKey(), entry.getValue()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonObject that = (JsonObject) o;
    return Objects.equals(members, that.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(members);
  }

}
