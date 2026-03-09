/**
 * Copyright (c) 2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.i18n;

import com.vaadin.flow.component.upload.UploadI18N;
import java.util.Locale;
import java.util.function.Consumer;
import lombok.NonNull;

/**
 * Helper class to create a fully translated {@link UploadI18N} instance for the upload component.
 */
public class UploadTranslation {
  private final UploadI18N upload = new UploadI18N();

  /**
   * Initializes the {@link UploadI18N} instance with translation keys.
   */
  public UploadTranslation() {
    UploadI18N.Uploading uploading = new UploadI18N.Uploading();
    upload.setUploading(uploading);

    UploadI18N.Uploading.Error uploadingError = new UploadI18N.Uploading.Error();
    uploading.setError(uploadingError);
    uploadingError.setForbidden(TranslationKeys.Upload.ERROR_FORBIDDEN__TEXT);
    uploadingError.setUnexpectedServerError(
        TranslationKeys.Upload.ERROR_UNEXPECTED_SERVER_ERROR__TEXT);
    uploadingError.setServerUnavailable(TranslationKeys.Upload.ERROR_SERVER_UNAVAILABLE__TEXT);

    UploadI18N.Uploading.Status status = new UploadI18N.Uploading.Status();
    uploading.setStatus(status);
    status.setConnecting(TranslationKeys.Upload.STATUS_CONNECTING__TEXT);
    status.setHeld(TranslationKeys.Upload.STATUS_HELD__TEXT);
    status.setProcessing(TranslationKeys.Upload.STATUS_PROCESSING__TEXT);
    status.setStalled(TranslationKeys.Upload.STATUS_STALLED__TEXT);

    UploadI18N.Uploading.RemainingTime remainingTime = new UploadI18N.Uploading.RemainingTime();
    uploading.setRemainingTime(remainingTime);
    remainingTime.setPrefix(TranslationKeys.Upload.REMAINING_TIME_PREFIX__TEXT);
    remainingTime.setUnknown(TranslationKeys.Upload.REMAINING_TIME_UNKNOWN__TEXT);

    UploadI18N.AddFiles addFiles = new UploadI18N.AddFiles();
    upload.setAddFiles(addFiles);
    addFiles.setOne(TranslationKeys.Upload.ADD_FILE__TEXT);
    addFiles.setMany(TranslationKeys.Upload.ADD_FILES__TEXT);

    UploadI18N.DropFiles dropFiles = new UploadI18N.DropFiles();
    upload.setDropFiles(dropFiles);
    dropFiles.setOne(TranslationKeys.Upload.DROP_FILE__TEXT);
    dropFiles.setMany(TranslationKeys.Upload.DROP_FILES__TEXT);

    UploadI18N.Error error = new UploadI18N.Error();
    upload.setError(error);
    error.setIncorrectFileType(TranslationKeys.Upload.ERROR_INCORRECT_FILE_TYPE__TEXT);
    error.setFileIsTooBig(TranslationKeys.Upload.ERROR_FILE_IS_TOO_BIG__TEXT);
    error.setTooManyFiles(TranslationKeys.Upload.ERROR_TOO_MANY_FILES__TEXT);

    UploadI18N.File file = new UploadI18N.File();
    upload.setFile(file);
    file.setRemove(TranslationKeys.Upload.FILE_REMOVE__TEXT);
    file.setRetry(TranslationKeys.Upload.FILE_RETRY__TEXT);
    file.setStart(TranslationKeys.Upload.FILE_START__TEXT);
  }

  /**
   * Allows customizing the {@link UploadI18N} instance with additional translation keys.
   *
   * @param customizer a consumer that accepts the {@link UploadI18N} instance for customization
   */
  public void customize(@NonNull Consumer<UploadI18N> customizer) {
    customizer.accept(this.upload);
  }

  /**
   * Returns a fully translated {@link UploadI18N} instance for the specified locale.
   *
   * @param locale the locale for which to retrieve the translations
   * @return a fully translated {@link UploadI18N} instance
   */
  public UploadI18N get(@NonNull Locale locale) {
    I18N i18N = I18N.current();
    UploadI18N result = new UploadI18N();

    UploadI18N.AddFiles addFiles = new UploadI18N.AddFiles();
    result.setAddFiles(addFiles);
    addFiles.setOne(i18N.getTranslation(upload.getAddFiles().getOne(), locale));
    addFiles.setMany(i18N.getTranslation(upload.getAddFiles().getMany(), locale));

    UploadI18N.DropFiles dropFiles = new UploadI18N.DropFiles();
    result.setDropFiles(dropFiles);
    dropFiles.setOne(i18N.getTranslation(upload.getDropFiles().getOne(), locale));
    dropFiles.setMany(i18N.getTranslation(upload.getDropFiles().getMany(), locale));

    UploadI18N.Error error = new UploadI18N.Error();
    result.setError(error);
    error.setIncorrectFileType(
        i18N.getTranslation(upload.getError().getIncorrectFileType(), locale));
    error.setFileIsTooBig(i18N.getTranslation(upload.getError().getFileIsTooBig(), locale));
    error.setTooManyFiles(i18N.getTranslation(upload.getError().getTooManyFiles(), locale));

    UploadI18N.File file = new UploadI18N.File();
    result.setFile(file);
    file.setRemove(i18N.getTranslation(upload.getFile().getRemove(), locale));
    file.setRetry(i18N.getTranslation(upload.getFile().getRetry(), locale));
    file.setStart(i18N.getTranslation(upload.getFile().getStart(), locale));

    UploadI18N.Uploading uploading = new UploadI18N.Uploading();
    result.setUploading(uploading);

    UploadI18N.Uploading.Error uploadingError = new UploadI18N.Uploading.Error();
    uploading.setError(uploadingError);
    uploadingError.setForbidden(
        i18N.getTranslation(upload.getUploading().getError().getForbidden(), locale));
    uploadingError.setUnexpectedServerError(
        i18N.getTranslation(upload.getUploading().getError().getUnexpectedServerError(), locale));
    uploadingError.setServerUnavailable(
        i18N.getTranslation(upload.getUploading().getError().getServerUnavailable(), locale));

    UploadI18N.Uploading.Status status = new UploadI18N.Uploading.Status();
    uploading.setStatus(status);
    status.setConnecting(
        i18N.getTranslation(upload.getUploading().getStatus().getConnecting(), locale));
    status.setHeld(i18N.getTranslation(upload.getUploading().getStatus().getHeld(), locale));
    status.setProcessing(
        i18N.getTranslation(upload.getUploading().getStatus().getProcessing(), locale));
    status.setStalled(i18N.getTranslation(upload.getUploading().getStatus().getStalled(), locale));

    UploadI18N.Uploading.RemainingTime remainingTime = new UploadI18N.Uploading.RemainingTime();
    uploading.setRemainingTime(remainingTime);
    remainingTime.setPrefix(
        i18N.getTranslation(upload.getUploading().getRemainingTime().getPrefix(), locale));
    remainingTime.setUnknown(
        i18N.getTranslation(upload.getUploading().getRemainingTime().getUnknown(), locale));

    return result;
  }
}
