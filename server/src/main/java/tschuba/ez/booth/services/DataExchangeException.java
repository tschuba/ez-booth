/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

/**
 * Exception thrown for errors during data exchange operations.
 */
public class DataExchangeException extends RuntimeException {
  public DataExchangeException(String message) {
    super(message);
  }
}
