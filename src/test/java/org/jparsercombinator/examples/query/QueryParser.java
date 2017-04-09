package org.jparsercombinator.examples.query;

import static org.jparsercombinator.ParserCombinators.regex;
import static org.jparsercombinator.ParserCombinators.skip;
import static org.jparsercombinator.ParserCombinators.string;

import org.jparsercombinator.ParserCombinator;
import org.jparsercombinator.ParserCombinatorReference;
import org.jparsercombinator.ParserCombinators;
import org.jparsercombinator.Parser;

class QueryParser implements Parser<Query> {

  private Parser<Query> parser;

  QueryParser() {
    ParserCombinatorReference<Query> queryParser = ParserCombinators.newRef();
    ParserCombinatorReference<Query> termParser = ParserCombinators.newRef();
    ParserCombinatorReference<Query> factorParser = ParserCombinators.newRef();

    ParserCombinator<Query> keyValueParser = regex("\\w+").skip(string(":")).next(regex("\\w*"))
        .map(result -> new KeyValueQuery(result.first, result.second));

    queryParser.setCombinator(termParser.many(string(" OR ")).map(OrQuery::new));
    termParser.setCombinator(factorParser.many(string(" AND ")).map(AndQuery::new));
    factorParser.setCombinator(string("NOT ").optional()
        .next(keyValueParser.or(skip(string("(")).next(queryParser).skip(string(")"))))
        .map(r -> r.first.isPresent() ? new NotQuery(r.second) : r.second));

    parser = queryParser.end();
  }

  @Override
  public Query apply(String input) {
    return parser.apply(input);
  }

}
