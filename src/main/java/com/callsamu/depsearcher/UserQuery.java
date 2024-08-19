package com.callsamu.depsearcher;

import jakarta.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserQuery {
  private String param;

  @Nullable private String version;

  private static Pattern firstPartPattern =
      Pattern.compile("^(?:[a-z0-9_\\\\-]+\\.)*([a-zA-Z0-9_\\\\-]+)$");

  private enum FirstPartType {
    FULLCLASS,
    GROUP,
    ANY,
  };

  UserQuery(String param, String version) {
    this.param = param;
    this.version = version;
  }

  private static FirstPartType getFirstPartType(String s) {
    final Matcher matcher = firstPartPattern.matcher(s);

    if (!matcher.find()) {
      return FirstPartType.ANY;
    }

    final String end = matcher.group(1);

    if (Character.isUpperCase(end.charAt(0))) {
      return FirstPartType.FULLCLASS;
    }

    return FirstPartType.GROUP;
  }

  public static UserQuery fromString(String s) throws IllegalArgumentException {
    final String[] parts = s.split(":");

    switch (parts.length) {
      case 0:
        throw new IllegalArgumentException("Empty query string");
      case 1:
        return switch (getFirstPartType(parts[0])) {
          case FULLCLASS -> new UserQuery("c:" + parts[0], null);
          case GROUP, ANY -> new UserQuery(parts[0], null);
        };
      case 2:
        return switch (getFirstPartType(parts[0])) {
          case GROUP -> new UserQuery("g:" + parts[0] + " AND a:" + parts[1], null);
          case FULLCLASS -> new UserQuery("c:" + parts[0], parts[1]);
          case ANY ->
              throw new IllegalArgumentException("Invalid full class name or group: " + parts[0]);
        };
      case 3:
        return switch (getFirstPartType(parts[0])) {
          case GROUP -> new UserQuery("g:" + parts[0] + " AND a:" + parts[1], parts[2]);
          case ANY, FULLCLASS -> throw new IllegalArgumentException("Invalid group on query: " + s);
        };
      default:
        throw new IllegalArgumentException("Invalid query format: " + s);
    }
  }

  public String toAPIQueryParam() {
    return this.param;
  }

  public Optional<String> getVersion() {
    return Optional.ofNullable(this.version);
  }
}
