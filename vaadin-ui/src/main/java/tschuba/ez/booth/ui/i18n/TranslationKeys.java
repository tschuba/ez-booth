/**
 * Copyright (c) 2023-2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.i18n;

public interface TranslationKeys {
    interface App {
        String TITLE = "app.title";
    }

    interface AppLayout {
        String EVENT_LINK__TOOLTIP_TEXT = "AppLayout.eventLink.tooltip";
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

    interface UpsertEventDialog {
        String TITLE = "UpsertEventDialog.title";
        String SAVE_BUTTON__TEXT = "UpsertEventDialog.saveButton.text";
        String CANCEL_BUTTON__TEXT = "UpsertEventDialog.cancelButton.text";
        String NOTIFICATION__SAVE_EVENT_FAILED = "UpsertEventDialog.notification.saveEventFailed";
    }

    interface UpsertEventForm {
        String DESCRIPTION_FIELD__LABEL = "UpsertEventForm.descriptionField.label";
        String DATE_FIELD__LABEL = "UpsertEventForm.dateField.label";
        String PARTICIPATION_FEE_FIELD__LABEL = "UpsertEventForm.participationFeeField.label";
        String SALES_FEE_FIELD__LABEL = "UpsertEventForm.salesFeeField.label";
        String FEES_ROUNDING_STEP_FIELD__LABEL = "UpsertEventForm.feesRoundingStepField.label";
        String NOTIFICATION__INVALID_DESCRIPTION =
                "UpsertEventForm.notification.invalidDescription";
        String NOTIFICATION__INVALID_DATE = "UpsertEventForm.notification.invalidDate";
        String NOTIFICATION__INVALID_PARTICIPATION_FEE =
                "UpsertEventForm.notification.invalidParticipationFee";
        String NOTIFICATION__INVALID_SALES_FEE = "UpsertEventForm.notification.invalidSalesFee";
        String NOTIFICATION__INVALID_FEES_ROUNDING_STEP =
                "UpsertEventForm.notification.invalidFeesRoundingStep";
    }

    interface EventSelection {
        String NOTIFICATION__NO_EVENT_SELECTED = "EventSelection.notification.noEventSelected";
    }

    interface EventSelectionView {
        String MENU_ITEM__TEXT = "EventSelectionView.menuItem.text";
        String TITLE = "EventSelectionView.title";
        String CREATE_BUTTON__TEXT = "EventSelectionView.createButton.text";
        String EDIT_BUTTON__TEXT = "EventSelectionView.editButton.text";
        String SELECT_BUTTON__TEXT = "EventSelectionView.selectButton.text";
        String INFO_BUTTON__TEXT = "EventSelectionView.infoButton.text";
        String CLOSE_BUTTON__TEXT = "EventSelectionView.closeButton.text";
        String OPEN_BUTTON__TEXT = "EventSelectionView.openButton.text";
        String DELETE_BUTTON__TEXT = "EventSelectionView.deleteButton.text";
        String DELETE_BUTTON_DISABLED__TEXT = "EventSelectionView.deleteButton.disabled.text";
        String CLOSE_EVENT_FAILED__MESSAGE = "EventSelectionView.closeEventFailed.message";
        String OPEN_EVENT_FAILED__MESSAGE = "EventSelectionView.openEventFailed.message";
        String DELETE_EVENT_FAILED__MESSAGE = "EventSelectionView.deleteEventFailed.message";
    }

    interface EventDetailsView {
        String TITLE = "EventDetailsView.title";
        String DESCRIPTION__LABEL = "EventDetailsView.description.label";
        String DATE__LABEL = "EventDetailsView.date.label";
        String PARTICIPATION_FEE__LABEL = "EventDetailsView.participationFee.label";
        String SALES_FEE__LABEL = "EventDetailsView.salesFee.label";
        String FEES_ROUNDING_STEP__LABEL = "EventDetailsView.feesRoundingStep.label";
        String TOTAL_VENDOR_COUNT__LABEL = "EventDetailsView.totalVendorCount.label";
        String TOTAL_PURCHASE_COUNT__LABEL = "EventDetailsView.totalPurchaseCount.label";
        String TOTAL_ITEM_COUNT__LABEL = "EventDetailsView.totalItemCount.label";
        String TOTAL_ITEM_SUM__LABEL = "EventDetailsView.totalItemSum.label";
        String TOTAL_PARTICIPATION_FEE__LABEL = "EventDetailsView.totalParticipationFee.label";
        String TOTAL_SALES_FEE__LABEL = "EventDetailsView.totalSalesFee.label";
        String TOTAL_REVENUE__LABEL = "EventDetailsView.totalRevenue.label";
        String NOTIFICATION__ILLEGAL_ARGUMENTS = "EventDetailsView.notification.illegalArguments";
        String EDIT_BUTTON__TEXT = "EventDetailsView.editButton.text";
        String EDIT_BUTTON_DISABLED__TEXT = "EventDetailsView.editButton.disabled.text";
        String OPEN_BUTTON__TEXT = "EventDetailsView.openButton.text";
        String CLOSE_BUTTON__TEXT = "EventDetailsView.closeButton.text";
        String DELETE_BUTTON__TEXT = "EventDetailsView.deleteButton.text";
        String DELETE_BUTTON_DISABLED__TEXT = "EventDetailsView.deleteButton.disabled.text";
        String CLOSE_EVENT_FAILED__MESSAGE = "EventDetailsView.closeEventFailed.message";
        String OPEN_EVENT_FAILED__MESSAGE = "EventDetailsView.openEventFailed.message";
        String DELETE_EVENT_FAILED__MESSAGE = "EventDetailsView.deleteEventFailed.message";
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
        String BUTTON_PRINT_ALL_RECEIPTS__TOOLTIP =
                "VendorReportView.buttonPrintAllReceipts.tooltip";
    }

    interface VendorReportCard {
        String BUTTON_PRINT_RECEIPT__TOOLTIP = "VendorReportView.buttonPrintReceipt.tooltip";
        String REVENUE__TOOLTIP = "VendorReportCard.revenue.tooltip";
        String ITEM_COUNT__TOOLTIP = "VendorReportCard.itemCount.tooltip";
    }

    interface VendorReportPrintView {
        String TITLE = "VendorReportPrintView.title";
        String VENDOR_ID__LABEL = "VendorReportPrintView.vendorId.label";
        String ITEM_COUNT__LABEL = "VendorReportPrintView.itemCount.label";
        String SUM__LABEL = "VendorReportPrintView.sum.label";
        String PARTICIPATION_FEE__LABEL = "PurchaseReceiptPrintView.purchaseFee.label";
        String SALES_FEE__LABEL = "PurchaseReceiptPrintView.salesFee.label";
        String TOTAL_REVENUE__LABEL = "PurchaseReceiptPrintView.totalRevenue.label";
        String NOTIFICATION__ILLEGAL_ARGUMENTS =
                "VendorReportPrintView.notification.illegalArguments";
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

    interface ExportImportView {
        String TITLE = "ExportImportView.title";
        String MENU_ITEM__TEXT = "ExportImportView.menuItem.text";
        String SYNC_HEADER__TEXT = "ExportImportView.syncHeader.text";
        String IMPORT_HEADER__TEXT = "ExportImportView.importHeader.text";
        String IMPORT_DESCRIPTION__TEXT = "ExportImportView.importDescription.text";
        String IMPORT_UPLOADER__I18N_PREFIX = "ExportImportView.importUploader.";
        String EXPORT_HEADER__TEXT = "ExportImportView.exportHeader.text";
        String EXPORT_DESCRIPTION__TEXT = "ExportImportView.exportDescription.text";
        String EXPORT_BUTTON__TEXT = "ExportImportView.exportButton.text";
        String NOTIFICATION__UPLOAD_SUCCEEDED = "ExportImportView.notification.uploadSucceeded";
        String NOTIFICATION__UPLOAD_FAILED = "ExportImportView.notification.uploadFailed";
        String NOTIFICATION__IMPORT_SUCCEEDED = "ExportImportView.notification.importSucceeded";
        String NOTIFICATION__IMPORT_FAILED = "ExportImportView.notification.importFailed";
        String NOTIFICATION__EXPORT_FAILED = "ExportImportView.notification.exportFailed";
        String NOTIFICATION__EXPORT_SUCCEEDED = "ExportImportView.notification.exportSucceeded";
    }

    interface DataSyncFragment {
        String HOST_TAB = "DataSyncFragment.hostTab.text";
        String SUBSCRIBER_TAB = "DataSyncFragment.subscriberTab.text";
    }

    interface DataSyncHostFragment {
        String SYNC_URL_LABEL__TEXT = "DataSyncHostFragment.syncUrlLabel.text";
        String SYNC_URL_DESCRIPTION__TEXT = "DataSyncHostFragment.syncUrlDescription.text";
        String SYNC_URL_DIVIDER__TEXT = "DataSyncHostFragment.syncUrlDivider.text";
        String SYNC_URL_COPIED_TO_CLIPBOARD__NOTIFICATION =
                "DataSyncHostFragment.syncUrlCopiedToClipboard.notification";
        String SYNC_DATA_BUTTON__TEXT = "DataSyncHostFragment.syncDataButton.text";
        String SYNC_DATA_DESCRIPTION__TEXT = "DataSyncHostFragment.syncDataDescription.text";
        String SYNC_IN_PROGRESS__TEXT = "DataSyncHostFragment.syncInProgress.text";
        String NO_SUBSCRIPTIONS__TEXT = "DataSyncHostFragment.noSubscriptions.text";
        String WAITING_FOR_SUBSCRIPTION__TEXT = "DataSyncHostFragment.waitingForSubscription.text";
        String SYNC_COMPLETED = "DataSyncHostFragment.notification.syncCompleted";
        String SYNC_FAILED = "DataSyncHostFragment.notification.syncFailed";
    }

    interface DataSyncSubscriberFragment {
        String SYNC_URL_LABEL__TEXT = "DataSyncSubscriberFragment.syncUrlLabel.text";
        String SYNC_URL_HELP__TEXT = "DataSyncSubscriberFragment.syncUrlHelp.text";
        String SYNC_JOIN_DESCRIPTION__TEXT = "DataSyncSubscriberFragment.joinSyncDescription.text";
        String SYNC_JOIN_BUTTON__TEXT = "DataSyncSubscriberFragment.joinSyncButton.text";
        String SYNC_LEAVE_DESCRIPTION__TEXT =
                "DataSyncSubscriberFragment.leaveSyncDescription.text";
        String SYNC_LEAVE_BUTTON__TEXT = "DataSyncSubscriberFragment.leaveSyncButton.text";
        String SYNC_JOIN_FAILED = "DataSyncSubscriberFragment.notification.joinSyncFailed";
    }

    interface InfoView {
        String TITLE = "InfoView.title";
        String MENU_ITEM__TEXT = "InfoView.menuItem.text";
        String FILE_STORAGE_BASE_PATH__LABEL = "InfoView.fileStorageBasePath.label";
        String HOMEPAGE__LABEL = "InfoView.homepage.label";
        String VERSION__LABEL = "InfoView.version.label";
        String VERSION_UNKNOWN__TEXT = "InfoView.versionUnknown.text";
    }
}
