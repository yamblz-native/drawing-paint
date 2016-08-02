package ru.yandex.yamblz.ui.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

@SuppressWarnings("WeakerAccess")
public class OpenFragment extends DialogFragment {

    @SuppressWarnings("WeakerAccess")
    public static final String ARGUMENT_FILES = "files";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String files[] = getArguments().getStringArray(ARGUMENT_FILES);
        assert files != null;

        OnFilePickedListener onFilePickedListener = (OnFilePickedListener) getParentFragment();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(files, (dialog, which) -> {
            if (onFilePickedListener != null) {
                onFilePickedListener.onFilePicked(files[which]);
            }
        });

        return builder.create();
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnFilePickedListener {
        void onFilePicked(String file);
    }
}
