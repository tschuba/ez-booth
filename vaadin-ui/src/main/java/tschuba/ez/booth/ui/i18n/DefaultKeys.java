/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.i18n;

public interface DefaultKeys {
    interface Common {
        String ADD = "Common.add.text";
        String EDIT = "Common.edit.text";
        String DELETE = "Common.delete.text";
        String CANCEL = "Common.cancel.text";
        String DISCARD = "Common.discard.text";
        String SAVE = "Common.save.text";
        String SELECT_ALL = "Common.select.all";
        String SELECT_NONE = "Common.select.none";
    }

    interface Filter {
        String MATCH_EXACT = "Filter.match.exact";
    }

    interface DataEditor {
        String ADD = Common.ADD;
        String EDIT = Common.EDIT;
        String DELETE = Common.DELETE;
        String NOTIFICATION__SAVE_FAILED = "DataEditor.notification.saveFailed";
        String NOTIFICATION__DELETE_FAILED = "DataEditor.notification.deleteFailed";
    }

    interface EditorActionBar {
        String CANCEL = Common.CANCEL;
        String DISCARD = Common.DISCARD;
        String EDIT = Common.EDIT;
        String SAVE = Common.SAVE;
    }

    interface MultiSelectCheckbox {
        String ALL_OR_NONE__LABEL = "MultiSelectCheckbox.allOrNone.label";
    }

    interface DatePicker {
        String TODAY = "DatePicker.today";
        String CANCEL = Common.CANCEL;
        String FIRST_DAY_OF_WEEK = "DatePicker.firstDayOfWeek";
    }
}
