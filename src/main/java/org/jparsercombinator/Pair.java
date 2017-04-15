package org.jparsercombinator;

import java.util.Objects;

/**
 * Contains two values {@code first} and {@code second} which are both public.
 *
 * @param <F> type of the first value
 * @param <S> type of the second value
 */
public class Pair<F, S> {

  public F first;
  public S second;

  /**
   * Builds a new pair
   *
   * @param first value of the pair
   * @param second value of the pair
   */
  public Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(first, pair.first) &&
        Objects.equals(second, pair.second);
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second);
  }

  @Override
  public String toString() {
    return "(" + first + ", " + second + ")";
  }

}
