package org.jparsercombinator;

public interface Result<T> {

  boolean isAccepted();

  T result();

  String remainingInput();

  String errorMessage();

}
