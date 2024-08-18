package com.callsamu.depsearcher;

import java.util.Optional;

public class UserQuery {
  private String param;
  private Optional<String> version;

  UserQuery(String param, Optional<String> version) {
    this.param = param;
    this.version = version;
  }

  public static boolean isGroupId(String s) {
    final String[] parts = s.split("\\.");
    final String last = parts[parts.length - 1];

    return Character.isUpperCase(last.charAt(0));
  }

  public static UserQuery fromString(String s) throws Exception {
    final String[] parts = s.split(":");

    if (isGroupId(parts[0])) {
      if (parts.length == 1) {
        return new UserQuery("c:" + parts[0], Optional.empty());
      }
      if (parts.length == 2) {
        return new UserQuery("c:" + parts[0], Optional.of(parts[1]));
      }
    }

    if (parts.length == 1) {
      return new UserQuery(parts[0], Optional.empty());
    }

    if (parts.length == 2 || parts.length == 3) {
      final String q = "" + "g:" + parts[0] + " AND " + "a:" + parts[1];

      final Optional<String> v = parts.length == 2 ? Optional.empty() : Optional.of(parts[2]);

      return new UserQuery(q, v);
    }

    throw new IllegalArgumentException();
  }

  public String toAPIQueryParam() {
    return this.param;
  }

  public Optional<String> getVersion() {
    return this.version;
  }
}
