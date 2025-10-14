/**
 * Copyright (c) 2023-2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.i18n;

public interface TranslationKeys {

  interface Common {
    String OR = "Common.or";
  }

  interface App {
    String TITLE = "app.title";
  }

  interface AppLayout {
    String BOOTH_LINK__TOOLTIP_TEXT = "AppLayout.boothLink.tooltip";
    String TOGGLE_THEME_BUTTON__TOOLTIP_TEXT = "AppLayout.toggleThemeButton.tooltip";
  }

  interface CheckoutItemForm {
    String VENDOR_FIELD__PLACEHOLDER = "CheckoutItemForm.vendorField.placeholder";
    String VENDOR_FIELD__LABEL = "CheckoutItemForm.vendorField.label";
    String PRICE_FIELD__PLACEHOLDER = "CheckoutItemForm.priceField.placeholder";
    String PRICE_FIELD__LABEL = "CheckoutItemForm.priceField.label";
    String CLEAR_BUTTON__TOOLTIP = "CheckoutItemForm.clearButton.tooltip";
    String HISTORY_TOGGLE_BUTTON__TOOLTIP = "CheckoutItemForm.historyToggleButton.tooltip";
  }

  interface CheckoutKeyPad {
    String CLEAR_BUTTON__TEXT = "CheckoutKeyPad.clearButton.text";
    String FINISH_BUTTON__TEXT = "CheckoutKeyPad.finishButton.text";
  }

  interface UpsertBoothDialog {
    String TITLE = "UpsertBoothDialog.title";
    String SAVE_BUTTON__TEXT = "UpsertBoothDialog.saveButton.text";
    String CANCEL_BUTTON__TEXT = "UpsertBoothDialog.cancelButton.text";
    String NOTIFICATION__SAVE_BOOTH_FAILED = "UpsertBoothDialog.notification.saveBoothFailed";
  }

  interface UpsertBoothForm {
    String DESCRIPTION_FIELD__LABEL = "UpsertBoothForm.descriptionField.label";
    String DATE_FIELD__LABEL = "UpsertBoothForm.dateField.label";
    String PARTICIPATION_FEE_FIELD__LABEL = "UpsertBoothForm.participationFeeField.label";
    String SALES_FEE_FIELD__LABEL = "UpsertBoothForm.salesFeeField.label";
    String FEES_ROUNDING_STEP_FIELD__LABEL = "UpsertBoothForm.feesRoundingStepField.label";
    String NOTIFICATION__INVALID_DESCRIPTION = "UpsertBoothForm.notification.invalidDescription";
    String NOTIFICATION__INVALID_DATE = "UpsertBoothForm.notification.invalidDate";
    String NOTIFICATION__INVALID_PARTICIPATION_FEE =
        "UpsertBoothForm.notification.invalidParticipationFee";
    String NOTIFICATION__INVALID_SALES_FEE = "UpsertBoothForm.notification.invalidSalesFee";
    String NOTIFICATION__INVALID_FEES_ROUNDING_STEP =
        "UpsertBoothForm.notification.invalidFeesRoundingStep";
  }

  interface BoothSelection {
    String NOTIFICATION__NO_BOOTH_SELECTED = "BoothSelection.notification.noBoothSelected";
  }

  interface BoothSelectionView {
    String MENU_ITEM__TEXT = "BoothSelectionView.menuItem.text";
    String TITLE = "BoothSelectionView.title";
    String CREATE_BUTTON__TEXT = "BoothSelectionView.createButton.text";
    String EDIT_BUTTON__TEXT = "BoothSelectionView.editButton.text";
    String SELECT_BUTTON__TEXT = "BoothSelectionView.selectButton.text";
    String INFO_BUTTON__TEXT = "BoothSelectionView.infoButton.text";
    String CLOSE_BUTTON__TEXT = "BoothSelectionView.closeButton.text";
    String OPEN_BUTTON__TEXT = "BoothSelectionView.openButton.text";
    String DELETE_BUTTON__TEXT = "BoothSelectionView.deleteButton.text";
    String DELETE_BUTTON_DISABLED__TEXT = "BoothSelectionView.deleteButton.disabled.text";
    String CLOSE_BOOTH_FAILED__MESSAGE = "BoothSelectionView.closeBoothFailed.message";
    String OPEN_BOOTH_FAILED__MESSAGE = "BoothSelectionView.openBoothFailed.message";
    String DELETE_BOOTH_FAILED__MESSAGE = "BoothSelectionView.deleteBoothFailed.message";
  }

  interface BoothDetailsView {
    String TITLE = "BoothDetailsView.title";
    String DESCRIPTION__LABEL = "BoothDetailsView.description.label";
    String DATE__LABEL = "BoothDetailsView.date.label";
    String PARTICIPATION_FEE__LABEL = "BoothDetailsView.participationFee.label";
    String SALES_FEE__LABEL = "BoothDetailsView.salesFee.label";
    String FEES_ROUNDING_STEP__LABEL = "BoothDetailsView.feesRoundingStep.label";
    String TOTAL_VENDOR_COUNT__LABEL = "BoothDetailsView.totalVendorCount.label";
    String TOTAL_PURCHASE_COUNT__LABEL = "BoothDetailsView.totalPurchaseCount.label";
    String TOTAL_ITEM_COUNT__LABEL = "BoothDetailsView.totalItemCount.label";
    String TOTAL_ITEM_SUM__LABEL = "BoothDetailsView.totalItemSum.label";
    String TOTAL_PARTICIPATION_FEE__LABEL = "BoothDetailsView.totalParticipationFee.label";
    String TOTAL_SALES_FEE__LABEL = "BoothDetailsView.totalSalesFee.label";
    String TOTAL_REVENUE__LABEL = "BoothDetailsView.totalRevenue.label";
    String TOTAL_PAYOUT__LABEL = "BoothDetailsView.totalPayout.label";
    String NOTIFICATION__ILLEGAL_ARGUMENTS = "BoothDetailsView.notification.illegalArguments";
    String EDIT_BUTTON__TEXT = "BoothDetailsView.editButton.text";
    String EDIT_BUTTON_DISABLED__TEXT = "BoothDetailsView.editButton.disabled.text";
    String OPEN_BUTTON__TEXT = "BoothDetailsView.openButton.text";
    String CLOSE_BUTTON__TEXT = "BoothDetailsView.closeButton.text";
    String DELETE_BUTTON__TEXT = "BoothDetailsView.deleteButton.text";
    String DELETE_BUTTON_DISABLED__TEXT = "BoothDetailsView.deleteButton.disabled.text";
    String CLOSE_BOOTH_FAILED__MESSAGE = "BoothDetailsView.closeBoothFailed.message";
    String OPEN_BOOTH_FAILED__MESSAGE = "BoothDetailsView.openBoothFailed.message";
    String DELETE_BOOTH_FAILED__MESSAGE = "BoothDetailsView.deleteBoothFailed.message";
  }

  interface Vendor {
    String ID__FORMAT_LONG = "Vendor.id.formatLong";
    String ID__FORMAT_SHORT = "Vendor.id.formatShort";
  }

  interface FieldComparator {
    String NO_FIELDS__EXCEPTION = "FieldComparator.exception.emptyFields";
    String NO_NULL_FIELDS__EXCEPTION = "FieldComparator.exception.noNullFields";
  }

  interface Notifications {
    String GENERIC_ERROR_MESSAGE = "Notifications.genericError.text";
    String COPIED_TO_CLIPBOARD__MESSAGE = "Notifications.copiedToClipboard.message";
    String SHOW_ERROR_BUTTON__TEXT = "Notifications.showErrorButton.text";
    String HIDE_ERROR_BUTTON__TEXT = "Notifications.hideErrorButton.text";
  }

  interface PurchaseSummary {
    String TITLE = "PurchaseSummary.title";
    String CHECKOUT_BUTTON__TEXT = "PurchaseSummary.checkoutButton.text";
    String CHECKOUT_BUTTON__TEXT_WITH_VALUE = "PurchaseSummary.checkoutButton.textWithValue";
    String CHECKOUT_BUTTON__TOOLTIP = "PurchaseSummary.checkoutButton.tooltip";
    String ITEM_COUNT__TEXT = "PurchaseSummary.itemCount.text";
  }

  interface CheckoutView {
    String MENU_ITEM__TEXT = "CheckoutView.menuItem.title";
    String TITLE = "CheckoutView.title";
    String PURCHASE_GRID__HEADER__DATE_TIME = "CheckoutView.purchaseGrid.header.dateTime";
    String PURCHASE_GRID__HEADER__VALUE = "CheckoutView.purchaseGrid.header.value";
    String PURCHASE_GRID__HEADER__ID = "CheckoutView.purchaseGrid.header.id";
    String BUTTON_PRINT_RECEIPT__TOOLTIP = "CheckoutView.buttonPrintReceipt.tooltip";
    String NOTIFICATION__PURCHASE_SUBMITTED = "CheckoutView.notification.purchaseSubmitted";
    String NOTIFICATION__PURCHASE_SUBMISSION_FAILED =
        "CheckoutView.notification.purchaseSubmissionFailed";
    String NOTIFICATION__UNSAVED_CHANGES = "CheckoutView.notification.unsavedChanges";
    String NOTIFICATION__PRINT_RECEIPT_TRIGGERED = "CheckoutView.notification.printReceipt";
  }

  interface CheckoutConfirmationDialog {
    String TITLE = "CheckoutConfirmationDialog.title";
    String TEXT = "CheckoutConfirmationDialog.text";
    String CONFIRM_BUTTON__TEXT = "CheckoutConfirmationDialog.confirmButton.text";
    String CANCEL_BUTTON__TEXT = "CheckoutConfirmationDialog.cancelButton.text";
    String PRINT_CHECKBOX__LABEL = "CheckoutConfirmationDialog.printCheckbox.label";
  }

  interface VendorReportView {
    String MENU_ITEM__TEXT = "VendorReportView.menuItem.title";
    String TITLE = "VendorReportView.title";
    String FILTER_FIELD__PLACEHOLDER = "VendorReportView.filterField.placeholder";
    String BUTTON_PRINT_ALL_RECEIPTS__TOOLTIP = "VendorReportView.buttonPrintAllReceipts.tooltip";
  }

  interface VendorReportCard {
    String BUTTON_PRINT_RECEIPT__TOOLTIP = "VendorReportView.buttonPrintReceipt.tooltip";
    String REVENUE__TOOLTIP = "VendorReportCard.revenue.tooltip";
    String ITEM_COUNT__TOOLTIP = "VendorReportCard.itemCount.tooltip";
  }

  interface VendorReportPrintView {
    String TITLE = "VendorReportPrintView.title";
    String NOTIFICATION__ILLEGAL_ARGUMENTS = "VendorReportPrintView.notification.illegalArguments";
    String NOTIFICATION__REPORT_GENERATION_FAILED =
        "VendorReportPrintView.notification.reportGenerationFailed";
  }

  interface PurchaseReceiptPrintView {
    String TITLE = "PurchaseReceiptPrintView.title";
    String PURCHASE_ID__LABEL = "PurchaseReceiptPrintView.purchaseId.label";
    String DATE_TIME__LABEL = "PurchaseReceiptPrintView.dateTime.label";
    String ITEM_COUNT__LABEL = "PurchaseReceiptPrintView.itemCount.label";
    String SUM__LABEL = "PurchaseReceiptPrintView.sum.label";
    String NOTIFICATION__ILLEGAL_ARGUMENTS =
        "PurchaseReceiptPrintView.notification.illegalArguments";
    String NOTIFICATION__PURCHASE_NOT_FOUND =
        "PurchaseReceiptPrintView.notification.purchaseNotFound";
  }

  interface DataExchangeView {
    String TITLE = "DataExchangeView.title";
    String MENU_ITEM__TEXT = "DataExchangeView.menuItem.text";

    interface SelfInfo {
      String TITLE = "DataExchangeView.SelfInfoCard.title";
      String ADDRESS_LABEL__TEXT = "DataExchangeView.SelfInfoCard.addressLabel.text";
      String SHORT_ADDRESS_LABEL__TEXT = "DataExchangeView.SelfInfoCard.shortAddressLabel.text";
    }

    interface Transfer {
      String TITLE = "DataExchangeView.TransferCard.title";
      String ADDRESS_FIELD__LABEL = "DataExchangeView.TransferCard.addressFieldLabel.text";
      String TRANSFER_BUTTON__LABEL = "DataExchangeView.TransferCard.transferButton.text";
      String TRANSFER_DESCRIPTION__TEXT = "DataExchangeView.TransferCard.description.text";
      String TRANSFER_IN_PROGRESS__TEXT = "DataExchangeView.TransferCard.transferInProgress.text";
      String NOTIFICATION__INVALID_ADDRESS =
          "DataExchangeView.TransferCard.notification.invalidValue";
      String NOTIFICATION__TRANSFER_FAILED =
          "DataExchangeView.TransferCard.notification.transferFailed";
      String NOTIFICATION__TRANSFER_COMPLETED =
          "DataExchangeView.TransferCard.notification.transferCompleted";
    }
  }
}
