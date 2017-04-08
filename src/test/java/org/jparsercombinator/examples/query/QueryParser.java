package org.jparsercombinator.examples.query;

import static org.jparsercombinator.Combinators.regex;
import static org.jparsercombinator.Combinators.skip;
import static org.jparsercombinator.Combinators.string;

import org.jparsercombinator.Combinator;
import org.jparsercombinator.CombinatorReference;
import org.jparsercombinator.Combinators;
import org.jparsercombinator.Parser;
import org.jparsercombinator.Parsers;

class QueryParser implements Parser<Query> {

  private Parser<Query> parser;

  QueryParser() {
    CombinatorReference<Query> queryParser = Combinators.newRef();
    CombinatorReference<Query> termParser = Combinators.newRef();
    CombinatorReference<Query> factorParser = Combinators.newRef();

    Combinator<Query> keyValueParser = regex("\\w+").skip(string(":")).next(regex("\\w*"))
        .map(result -> new KeyValueQuery(result.first, result.second));

    queryParser.setCombinator(termParser.many(string(" OR ")).map(OrQuery::new));
    termParser.setCombinator(factorParser.many(string(" AND ")).map(AndQuery::new));
    factorParser.setCombinator(string("NOT ").optional()
        .next(keyValueParser.or(skip(string("(")).next(queryParser).skip(string(")"))))
        .map(r -> r.first.isPresent() ? new NotQuery(r.second) : r.second));

    parser = Parsers.parser(queryParser);
  }

  @Override
  public Query apply(String input) {
    return parser.apply(input);
  }

}
