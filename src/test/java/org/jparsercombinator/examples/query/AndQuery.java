package org.jparsercombinator.examples.query;

import static java.util.stream.Collectors.joining;

import java.util.List;

class AndQuery implements Query {

  private List<Query> queries;

  AndQuery(List<Query> queries) {
    this.queries = queries;
  }

  @Override
  public String toString() {
    return queries.stream().map(Object::toString).collect(joining(" AND ", "(", ")"));
  }

}