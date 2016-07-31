package ru.yandex.yamblz.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.yandex.yamblz.ui.activities.MainActivity;

public class OpenFragment extends DialogFragment {

    @SuppressWarnings("WeakerAccess")
    public static final String ARGUMENT_FILES = "files";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String files[] = getArguments().getStringArray(ARGUMENT_FILES);
        assert files != null;

        OnFilePickedListener onFilePickedListener =
                ((MainActivity) getActivity()).getOnFilePickedListener();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(files, (dialog, which) -> {
            if (onFilePickedListener != null) {
                onFilePickedListener.onFilePicked(files[which]);
            }
        });

        return builder.create();
    }

    public interface OnFilePickedListener {
        void onFilePicked(String file);
    }
}
