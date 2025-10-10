/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.io.Writer;
import lombok.NonNull;
import tschuba.ez.booth.services.ReportingException;

public interface ReportTemplate<T> {
  /**
   * @param writer the writer to write the report to
   * @param data the data to render the report with
   * @throws ReportingException if an error occurs during rendering
   */
  void render(@NonNull Writer writer, @NonNull T data) throws ReportingException;
}
