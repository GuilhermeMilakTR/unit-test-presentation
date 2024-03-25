package com.demo.uuid;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class UuidHelper {

  public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
  public static final String EMPTY_STRING_UUID = "00000000000000000000000000000000";
  private static final Pattern PATTERN = Pattern.compile("^(.{8})(.{4})(.{4})(.{4})(.{12})$");
  private static final Pattern VALID_NO_DASH = Pattern.compile("[0-9a-fA-F]{32}");
  private static final String REPLACEMENT = "$1-$2-$3-$4-$5";

  private UuidHelper() {
  }


  public static UUID fromString(final String string) {
    if (string == null) {
      return null;
    }

    // make sure that string contains no dashes
    String noDashesString = string.replaceAll("-", "");
    if (VALID_NO_DASH.matcher(noDashesString).matches()) {
      return UUID.fromString(PATTERN.matcher(noDashesString).replaceAll(REPLACEMENT).toLowerCase());
    }

    return null;
  }

  public static String fromUUID(final UUID uuid) {
    Objects.requireNonNull(uuid, "'uuid' cannot be null");
    return uuid.toString().replaceAll("-", "").toUpperCase();
  }

  public static UUID randomUuid() {
    return UUID.randomUUID();
  }

  public static String toBluemoonUuid(String uuid) {
    return fromUUID(fromString(uuid));
  }

}
