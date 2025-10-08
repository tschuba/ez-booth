/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.i18n;

import static tschuba.ez.booth.ui.i18n.I18N.i18N;

import com.vaadin.flow.component.upload.UploadI18N;
import java.util.Locale;

public class Upload {
    public static final String UPLOAD_I18N_SUFFIX_ADD_FILE = "addFile";
    public static final String UPLOAD_I18N_SUFFIX_INCORRECT_FILE_TYPE = "incorrectFileType";
    public static final String UPLOAD_I18N_SUFFIX_STATUS_PROCESSING = "statusProcessing";

    public static UploadI18N upload(String prefix, Locale locale) {
        UploadI18N.AddFiles addFiles = new UploadI18N.AddFiles();
        I18N i18N = i18N();
        addFiles.setOne(i18N.getTranslation(prefix + UPLOAD_I18N_SUFFIX_ADD_FILE, locale));

        UploadI18N.Error error = new UploadI18N.Error();
        error.setIncorrectFileType(
                i18N.getTranslation(prefix + UPLOAD_I18N_SUFFIX_INCORRECT_FILE_TYPE, locale));

        UploadI18N.Uploading.Status uploadingStatus = new UploadI18N.Uploading.Status();
        uploadingStatus.setProcessing(
                i18N.getTranslation(prefix + UPLOAD_I18N_SUFFIX_STATUS_PROCESSING, locale));

        UploadI18N.Uploading uploading = new UploadI18N.Uploading();
        uploading.setStatus(uploadingStatus);

        UploadI18N uploadI18N = new UploadI18N();
        uploadI18N.setAddFiles(addFiles);
        uploadI18N.setError(error);
        uploadI18N.setUploading(uploading);
        return uploadI18N;
    }
}
