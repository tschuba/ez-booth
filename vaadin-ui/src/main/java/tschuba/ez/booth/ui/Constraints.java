/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui;

import static lombok.AccessLevel.PRIVATE;
import static tschuba.ez.booth.ui.util.Patterns.dataExchange;

import lombok.NoArgsConstructor;
import tschuba.ez.booth.ui.util.Patterns;

@NoArgsConstructor(access = PRIVATE)
public class Constraints {

  @NoArgsConstructor(access = PRIVATE)
  public static class Events {

    @NoArgsConstructor(access = PRIVATE)
    public static class Description {
      public static final String ALLOWED_CHARS_PATTERN = "\\w|[- ÄÖÜäöüß]";
    }
  }

  @NoArgsConstructor(access = PRIVATE)
  public static class Vendors {
    public static final String ALLOWED_CHARS_PATTERN = "\\d";
  }

  @NoArgsConstructor(access = PRIVATE)
  public static class DataExchange {

    @NoArgsConstructor(access = PRIVATE)
    public static class Transfer {
      public static final String ADDRESS_PATTERN =
          Patterns.dataExchange()
              .address()
              .or(dataExchange().encodedAddress())
              .wholeInput()
              .pattern();
    }
  }
}
