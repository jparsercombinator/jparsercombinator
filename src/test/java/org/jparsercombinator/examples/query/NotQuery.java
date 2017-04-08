package org.jparsercombinator.examples.query;

class NotQuery implements Query {

  private Query query;

  NotQuery(Query query) {
    this.query = query;
  }

  @Override
  public String toString() {
    return "(NOT " + query + ")";
  }

}
