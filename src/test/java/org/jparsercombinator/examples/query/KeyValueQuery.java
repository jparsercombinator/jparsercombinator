package org.jparsercombinator.examples.query;

class KeyValueQuery implements Query {

  private String key;
  private String value;

  KeyValueQuery(String key, String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String toString() {
    return key + " = " + value;
  }

}