package org.jparsercombinator.examples.json;

class JsonNull implements JsonElement {

  @Override
  public boolean equals(Object o) {
    return this == o || (o != null && getClass() == o.getClass());
  }

  @Override
  public int hashCode() {
    return 0;
  }

}
