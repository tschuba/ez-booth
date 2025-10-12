/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

public final class DataModel {

  private DataModel() {}

  @Builder(toBuilder = true)
  public record Booth(
      @NonNull Key key,
      @NonNull String description,
      @NonNull LocalDate date,
      @NonNull BigDecimal participationFee,
      @NonNull BigDecimal salesFee,
      @NonNull BigDecimal feesRoundingStep,
      boolean closed,
      @Nullable LocalDateTime closedOn) {

    @Builder(toBuilder = true)
    public record Key(@NonNull String boothId) implements Comparable<Key> {
      @Override
      public int compareTo(@NonNull Key key) {
        return Comparator.comparing(Key::boothId).compare(this, key);
      }
    }
  }

  @Builder(toBuilder = true)
  public record Purchase(
      @EqualsAndHashCode.Include @NonNull Key key,
      @NonNull BigDecimal value,
      @NonNull LocalDateTime purchasedOn,
      @NonNull List<PurchaseItem> items) {

    @Builder(toBuilder = true)
    public record Key(Booth.Key booth, String purchaseId) implements Comparable<Key> {
      @Override
      public int compareTo(@NonNull Key key) {
        return Comparator.comparing(Key::booth).thenComparing(Key::purchaseId).compare(this, key);
      }
    }
  }

  @Builder(toBuilder = true)
  public record PurchaseItem(
      @EqualsAndHashCode.Include @NonNull Key key,
      @EqualsAndHashCode.Include @NonNull Vendor.Key vendor,
      BigDecimal price,
      LocalDateTime purchasedOn) {

    @Builder(toBuilder = true)
    public record Key(Purchase.Key purchase, String itemId) implements Comparable<Key> {
      @Override
      public int compareTo(@NonNull Key key) {
        return Comparator.comparing(Key::purchase).thenComparing(Key::itemId).compare(this, key);
      }
    }
  }

  @Builder(toBuilder = true)
  public record Vendor(@EqualsAndHashCode.Include @NonNull Key key) {

    @Builder(toBuilder = true)
    public record Key(@NonNull Booth.Key booth, @NonNull String vendorId) {}
  }
}
