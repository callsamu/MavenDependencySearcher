package com.callsamu.depsearcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UserQueryTest {
  @ParameterizedTest
  @CsvSource({
    "com.google.inject:guice, g:com.google.inject AND a:guice,",
    "com.google.inject:guice:3.0, g:com.google.inject AND a:guice, 3.0",
    "com.google.inject.Guice, c:com.google.inject.Guice,",
    "com.google.inject.Guice:3.0, c:com.google.inject.Guice, 3.0",
  })
  public void shouldParseValid(String constructorString, String APIQueryParam, String version) {
    final UserQuery q = UserQuery.fromString(constructorString);

    assertAll(
        constructorString + " should parse",
        () -> assertEquals(q.toAPIQueryParam(), APIQueryParam),
        () -> assertEquals(q.getVersion(), Optional.ofNullable(version)));
  }

  @ParameterizedTest
  @CsvSource({"foo bar:artifact", "foo:bar:foobar:1.0", "com.foo.Class:artifact:1.0"})
  public void shouldThrowOnInvalid(String constructorString) {
    assertThrows(IllegalArgumentException.class, () -> UserQuery.fromString(constructorString));
  }
}
